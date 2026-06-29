import { useState, type ReactNode } from 'react';
import { X, Camera, Eye, EyeOff, Lock, Mail, Phone, User } from 'lucide-react';

interface Props {
  isDark: boolean;
  onClose: () => void;
  initialName: string;
  initialEmail: string;
  initialPhone: string;
  initials: string;
}

interface FieldProps {
  label: string;
  value: string;
  onChange: (v: string) => void;
  type?: string;
  icon: ReactNode;
  end?: ReactNode;
  isDark: boolean;
}

function DialogField({ label, value, onChange, type = 'text', icon, end, isDark }: FieldProps) {
  const [focused, setFocused] = useState(false);
  const border = focused ? (isDark ? '#7EAAFF' : '#1565C0') : (isDark ? '#3A3D45' : '#D1D5DB');
  const bg = isDark ? '#2A2D38' : '#FAFBFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const labelColor = focused ? (isDark ? '#7EAAFF' : '#1565C0') : muted;

  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: 10,
        background: bg,
        border: `1.5px solid ${border}`,
        borderRadius: 8,
        padding: '8px 12px',
        minHeight: 48,
        transition: 'border-color 0.15s',
      }}
    >
      <span style={{ color: muted, display: 'flex', flexShrink: 0 }}>{icon}</span>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 10, color: labelColor, fontFamily: 'Roboto, system-ui, sans-serif', marginBottom: 1 }}>
          {label}
        </div>
        <input
          type={type}
          value={value}
          onChange={e => onChange(e.target.value)}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          style={{
            width: '100%',
            background: 'none',
            border: 'none',
            outline: 'none',
            fontSize: 13.5,
            color: onSurface,
            fontFamily: 'Roboto, system-ui, sans-serif',
            padding: 0,
          }}
        />
      </div>
      {end && <span style={{ flexShrink: 0 }}>{end}</span>}
    </div>
  );
}

export function EditProfileDialog({ isDark, onClose, initialName, initialEmail, initialPhone, initials }: Props) {
  const [name, setName] = useState(initialName);
  const [email, setEmail] = useState(initialEmail);
  const [phone, setPhone] = useState(initialPhone);
  const [newPass, setNewPass] = useState('');
  const [confirmPass, setConfirmPass] = useState('');
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const primary = isDark ? '#7EAAFF' : '#1565C0';
  const surface = isDark ? '#1E1E2A' : '#FFFFFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const border = isDark ? '#2A2D35' : '#E5E7EB';
  const overlay = isDark ? 'rgba(0,0,0,0.7)' : 'rgba(0,0,0,0.45)';
  const avatarBg = isDark ? '#0D2B6B' : '#DBEAFE';

  return (
    <div
      style={{
        position: 'absolute',
        inset: 0,
        background: overlay,
        display: 'flex',
        alignItems: 'flex-end',
        zIndex: 100,
        backdropFilter: 'blur(2px)',
      }}
      onClick={e => { if (e.target === e.currentTarget) onClose(); }}
    >
      <div
        style={{
          width: '100%',
          background: surface,
          borderRadius: '24px 24px 0 0',
          overflow: 'hidden',
          maxHeight: '92%',
          display: 'flex',
          flexDirection: 'column',
          fontFamily: 'Roboto, system-ui, sans-serif',
        }}
      >
        {/* Handle */}
        <div style={{ display: 'flex', justifyContent: 'center', padding: '10px 0 6px' }}>
          <div style={{ width: 36, height: 4, borderRadius: 2, background: isDark ? '#3A3D45' : '#D1D5DB' }} />
        </div>

        {/* Header */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 20px 14px' }}>
          <div>
            <div style={{ fontSize: 17, fontWeight: 700, color: onSurface }}>Edit Profile</div>
            <div style={{ fontSize: 12, color: muted, marginTop: 2 }}>Update your account information</div>
          </div>
          <button
            onClick={onClose}
            style={{
              width: 34,
              height: 34,
              borderRadius: '50%',
              background: isDark ? '#252830' : '#F5F7FA',
              border: 'none',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: muted,
            }}
          >
            <X size={16} />
          </button>
        </div>

        {/* Divider */}
        <div style={{ height: 1, background: border }} />

        {/* Scrollable form */}
        <div style={{ overflowY: 'auto', padding: '20px 20px 8px', display: 'flex', flexDirection: 'column', gap: 12 }}>
          {/* Avatar */}
          <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 4 }}>
            <div style={{ position: 'relative', display: 'inline-block' }}>
              <div
                style={{
                  width: 72,
                  height: 72,
                  borderRadius: '50%',
                  background: avatarBg,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  border: `2px solid ${isDark ? '#3A4D7A' : '#BFCFE8'}`,
                }}
              >
                <span style={{ fontSize: 24, fontWeight: 700, color: primary }}>{initials}</span>
              </div>
              <button
                style={{
                  position: 'absolute',
                  bottom: 0,
                  right: 0,
                  width: 26,
                  height: 26,
                  borderRadius: '50%',
                  background: primary,
                  border: `2px solid ${surface}`,
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <Camera size={13} color="#fff" />
              </button>
            </div>
          </div>

          {/* Account info section */}
          <div style={{ fontSize: 11, fontWeight: 600, color: muted, letterSpacing: '0.06em', textTransform: 'uppercase' }}>
            Account Info
          </div>

          <DialogField label="Username" value={name} onChange={setName} isDark={isDark} icon={<User size={15} />} />
          <DialogField label="Email Address" value={email} onChange={setEmail} type="email" isDark={isDark} icon={<Mail size={15} />} />
          <DialogField label="Phone Number" value={phone} onChange={setPhone} type="tel" isDark={isDark} icon={<Phone size={15} />} />

          {/* Password section */}
          <div style={{ fontSize: 11, fontWeight: 600, color: muted, letterSpacing: '0.06em', textTransform: 'uppercase', marginTop: 4 }}>
            Change Password
          </div>
          <div style={{ fontSize: 11.5, color: muted, marginTop: -6 }}>
            Leave blank to keep your current password
          </div>

          <DialogField
            label="New Password"
            value={newPass}
            onChange={setNewPass}
            type={showNew ? 'text' : 'password'}
            isDark={isDark}
            icon={<Lock size={15} />}
            end={
              <button onClick={() => setShowNew(p => !p)} style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0, color: muted, display: 'flex' }}>
                {showNew ? <EyeOff size={14} /> : <Eye size={14} />}
              </button>
            }
          />
          <DialogField
            label="Confirm Password"
            value={confirmPass}
            onChange={setConfirmPass}
            type={showConfirm ? 'text' : 'password'}
            isDark={isDark}
            icon={<Lock size={15} />}
            end={
              <button onClick={() => setShowConfirm(p => !p)} style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0, color: muted, display: 'flex' }}>
                {showConfirm ? <EyeOff size={14} /> : <Eye size={14} />}
              </button>
            }
          />
        </div>

        {/* Action buttons */}
        <div style={{ padding: '14px 20px 24px', display: 'flex', gap: 10 }}>
          <button
            onClick={onClose}
            style={{
              flex: 1,
              padding: '12px',
              background: 'transparent',
              color: muted,
              border: `1.5px solid ${border}`,
              borderRadius: 10,
              fontSize: 14,
              fontWeight: 500,
              cursor: 'pointer',
              fontFamily: 'Roboto, system-ui, sans-serif',
            }}
          >
            Cancel
          </button>
          <button
            onClick={onClose}
            style={{
              flex: 2,
              padding: '12px',
              background: primary,
              color: '#fff',
              border: 'none',
              borderRadius: 10,
              fontSize: 14,
              fontWeight: 600,
              cursor: 'pointer',
              fontFamily: 'Roboto, system-ui, sans-serif',
              boxShadow: isDark ? '0 4px 14px rgba(126,170,255,0.2)' : '0 4px 14px rgba(21,101,192,0.25)',
            }}
          >
            Save Changes
          </button>
        </div>
      </div>
    </div>
  );
}
