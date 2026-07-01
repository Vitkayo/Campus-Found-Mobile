#!/usr/bin/env python3
"""Upload demo lost-and-found items from Pic Lost Found/ to MockAPI."""

from __future__ import annotations

import base64
import io
import json
import sys
import time
import urllib.error
import urllib.request
from datetime import datetime, timedelta, timezone
from pathlib import Path

from PIL import Image

API_BASE = "https://6a1460d76c7db8aac05469d9.mockapi.io"
MAX_IMAGE_BYTES = 70_000
MAX_EDGE_PX = 960
PROJECT_ROOT = Path(__file__).resolve().parents[1]
PHOTO_DIR = PROJECT_ROOT / "Pic Lost Found"

LOCATIONS = [
    "Library, 2nd floor",
    "Cafeteria entrance",
    "Building A, Room 610",
    "Main parking lot",
    "Sports complex",
    "Computer lab, Building B",
    "Student lounge",
    "Campus gate 2",
    "Auditorium hallway",
    "Science building lobby",
]

CONTACTS = [
    "telegram: @campus_found",
    "facebook: campus.found.help",
    "012 345 678",
    "vit@gmail.com",
    "demo@test.com",
]

REPORTERS = ["Vit", "Demo User", "Campus Found", "Sokha", "Dara"]


def categorize(filename: str) -> str:
    name = filename.lower()
    if "ticket" in name:
        return "Ticket"
    if any(k in name for k in ("phone", "earpod", "charger", "labtop", "laptop")):
        return "Electronics"
    if any(
        k in name
        for k in ("student id", "national id", "id card", "parking card", "some id")
    ):
        return "Card ID"
    if "wallet" in name or "money" in name:
        return "Wallet"
    if "key" in name:
        return "Keys"
    if any(
        k in name
        for k in (
            "hoodie",
            "jacket",
            "helmet",
            "glasses",
            "umbrella",
            "earing",
            "sun glass",
        )
    ):
        return "Clothing & Accessories"
    if any(k in name for k in ("book", "folder", "file", "document")):
        return "Books & Stationery"
    return "Other"


def title_from_filename(filename: str) -> str:
    return Path(filename).stem.strip().title()


def description_for(title: str, category: str, status: str) -> str:
    action = "lost" if status == "lost" else "found"
    return (
        f"{title} was {action} on campus. "
        f"Category: {category}. Please check the photo and contact the reporter if this is yours."
    )


def compress_image(path: Path) -> str:
    with Image.open(path) as img:
        img = img.convert("RGB")
        width, height = img.size

        for max_edge in (MAX_EDGE_PX, 720, 640, 480):
            resized = img
            longest = max(width, height)
            if longest > max_edge:
                scale = max_edge / longest
                resized = img.resize(
                    (max(1, int(width * scale)), max(1, int(height * scale))),
                    Image.Resampling.LANCZOS,
                )

            for quality in range(85, 24, -7):
                buffer = io.BytesIO()
                resized.save(buffer, format="JPEG", quality=quality, optimize=True)
                data = buffer.getvalue()
                if len(data) <= MAX_IMAGE_BYTES:
                    encoded = base64.b64encode(data).decode("ascii")
                    return f"data:image/jpeg;base64,{encoded}"

    raise RuntimeError(f"Could not compress {path.name} under {MAX_IMAGE_BYTES} bytes")


def api_request(method: str, path: str, payload: dict | None = None) -> object:
    url = f"{API_BASE}{path}"
    data = None if payload is None else json.dumps(payload).encode("utf-8")
    request = urllib.request.Request(
        url,
        data=data,
        method=method,
        headers={"Content-Type": "application/json", "Accept": "application/json"},
    )
    with urllib.request.urlopen(request, timeout=120) as response:
        body = response.read().decode("utf-8")
        return json.loads(body) if body else None


def build_items() -> list[dict]:
    if not PHOTO_DIR.is_dir():
        raise FileNotFoundError(f"Photo folder not found: {PHOTO_DIR}")

    files = sorted(
        p for p in PHOTO_DIR.iterdir() if p.suffix.lower() in {".jpg", ".jpeg", ".png", ".webp"}
    )
    if not files:
        raise FileNotFoundError(f"No images found in {PHOTO_DIR}")

    base_time = datetime.now(timezone.utc)
    items: list[dict] = []

    for index, path in enumerate(files):
        category = categorize(path.name)
        title = title_from_filename(path.name)
        status = "lost" if index % 2 == 0 else "found"
        created = base_time - timedelta(hours=index * 3)
        date_str = created.strftime("%Y-%m-%d")

        items.append(
            {
                "title": title,
                "description": description_for(title, category, status),
                "category": category,
                "status": status,
                "location": LOCATIONS[index % len(LOCATIONS)],
                "imageUrl": compress_image(path),
                "date": date_str,
                "postedBy": REPORTERS[index % len(REPORTERS)],
                "createdAt": str(int(created.timestamp() * 1000)),
                "contactInfo": CONTACTS[index % len(CONTACTS)],
            }
        )

    return items


def seed(clear_existing: bool = False) -> None:
    if clear_existing:
        existing = api_request("GET", "/items")
        if isinstance(existing, list):
            print(f"Deleting {len(existing)} existing items...")
            for item in existing:
                item_id = item.get("id")
                if item_id:
                    api_request("DELETE", f"/items/{item_id}")
                    time.sleep(0.15)

    items = build_items()
    print(f"Uploading {len(items)} items from {PHOTO_DIR.name}/ ...")

    success = 0
    for index, item in enumerate(items, start=1):
        try:
            created = api_request("POST", "/items", item)
            item_id = created.get("id") if isinstance(created, dict) else "?"
            print(f"[{index}/{len(items)}] OK {item['title']} ({item['category']}) -> id {item_id}")
            success += 1
            time.sleep(0.2)
        except urllib.error.HTTPError as exc:
            body = exc.read().decode("utf-8", errors="replace")
            print(f"[{index}/{len(items)}] FAIL {item['title']}: HTTP {exc.code} {body[:200]}")
        except Exception as exc:  # noqa: BLE001
            print(f"[{index}/{len(items)}] FAIL {item['title']}: {exc}")

    print(f"Done. Uploaded {success}/{len(items)} items.")


if __name__ == "__main__":
    clear = "--clear" in sys.argv
    seed(clear_existing=clear)
