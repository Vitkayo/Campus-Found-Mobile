param(
    [string]$Device = "",
    [string]$FolderName = "CampusFound"
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$photoDir = Join-Path $projectRoot "Pic Lost Found"
$sdkRoot = $env:ANDROID_HOME
if ([string]::IsNullOrWhiteSpace($sdkRoot)) {
    $sdkRoot = Join-Path $env:LOCALAPPDATA "Android\Sdk"
}

$adb = Join-Path $sdkRoot "platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    throw "adb not found at $adb"
}

$adbArgs = @()
if ($Device) {
    $adbArgs += "-s", $Device
}

function Invoke-Adb {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Args)
    & $adb @adbArgs @Args
    if ($LASTEXITCODE -ne 0) {
        throw "adb failed: adb $($adbArgs -join ' ') $($Args -join ' ')"
    }
}

if (-not (Test-Path $photoDir)) {
    throw "Photo folder not found: $photoDir"
}

$photos = Get-ChildItem -Path $photoDir -File | Where-Object {
    $_.Extension -match '^\.(jpg|jpeg|png|webp)$'
} | Sort-Object Name

if ($photos.Count -eq 0) {
    throw "No images found in $photoDir"
}

$remoteDir = "/sdcard/Pictures/$FolderName"
Invoke-Adb shell "mkdir -p $remoteDir" | Out-Null

Write-Host "Pushing $($photos.Count) photos to emulator:$remoteDir"
$pushed = 0
foreach ($photo in $photos) {
    $remotePath = "$remoteDir/$($photo.Name)"
    Invoke-Adb push $photo.FullName $remotePath | Out-Null
    $pushed++
    Write-Host "  [$pushed/$($photos.Count)] $($photo.Name)"
}

Write-Host "Refreshing gallery..."
foreach ($photo in $photos) {
    $encodedName = $photo.Name -replace ' ', '%20'
    $uri = "file://$remoteDir/$encodedName"
    Invoke-Adb shell "am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d `"$uri`"" | Out-Null
}

Write-Host ""
Write-Host "Done. Open the emulator Gallery or Photos app -> Pictures -> $FolderName"
Write-Host "Or pick them in Campus Found when you tap Choose photos."
