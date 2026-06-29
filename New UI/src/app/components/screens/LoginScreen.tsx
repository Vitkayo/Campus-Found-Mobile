import { useState, type ReactNode } from 'react';
import { Eye, EyeOff, Lock, Mail, User, GraduationCap, KeyRound, CreditCard, Smartphone } from 'lucide-react';
import type { AppProps } from '../../App';

interface Props extends AppProps {
  onLogin: () => void;
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

function Field({ label, value, onChange, type = 'text', icon, end, isDark }: FieldProps) {
  const [focused, setFocused] = useState(false);
  const border = focused ? (isDark ? '#7EAAFF' : '#1565C0') : (isDark ? '#3A3D45' : '#D1D5DB');
  const focusBg = focused ? (isDark ? 'rgba(126,170,255,0.06)' : 'rgba(21,101,192,0.03)') : (isDark ? '#252830' : '#FAFBFF');
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const labelColor = focused ? (isDark ? '#7EAAFF' : '#1565C0') : muted;

  return (
    <div>
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: 10,
          background: focusBg,
          border: `1.5px solid ${border}`,
          borderRadius: 10,
          padding: '10px 14px',
          transition: 'border-color 0.2s, background 0.2s',
          boxShadow: focused ? (isDark ? '0 0 0 3px rgba(126,170,255,0.12)' : '0 0 0 3px rgba(21,101,192,0.1)') : 'none',
        }}
        onFocusCapture={() => setFocused(true)}
        onBlurCapture={() => setFocused(false)}
      >
        <span style={{ color: focused ? (isDark ? '#7EAAFF' : '#1565C0') : muted, display: 'flex', flexShrink: 0, transition: 'color 0.2s' }}>
          {icon}
        </span>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 10, color: labelColor, fontFamily: 'Roboto, system-ui, sans-serif', marginBottom: 1, transition: 'color 0.2s', fontWeight: 500, letterSpacing: '0.04em' }}>
            {label.toUpperCase()}
          </div>
          <input
            type={type}
            value={value}
            onChange={e => onChange(e.target.value)}
            style={{
              width: '100%',
              background: 'none',
              border: 'none',
              outline: 'none',
              fontSize: 14,
              color: onSurface,
              fontFamily: 'Roboto, system-ui, sans-serif',
              padding: 0,
            }}
          />
        </div>
        {end && <span style={{ flexShrink: 0 }}>{end}</span>}
      </div>
    </div>
  );
}

export function LoginScreen({ isDark, navigate, onLogin }: Props) {
  const [tab, setTab] = useState<'login' | 'register'>('login');
  const [showPass, setShowPass] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [confirm, setConfirm] = useState('');

  const bg = isDark ? '#0E1016' : '#F5F7FA';
  const surface = isDark ? '#1A1C25' : '#FFFFFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const primary = isDark ? '#7EAAFF' : '#1565C0';
  const border = isDark ? '#2A2D35' : '#E5E7EB';
  const tabBg = isDark ? '#252830' : '#EEF2F8';
  const activeTabBg = isDark ? '#A8C7FA' : '#FFFFFF';
  const activeTabText = isDark ? '#0D1526' : '#1C1B1F';

  return (
    <div
      style={{
        height: '100%',
        background: bg,
        display: 'flex',
        flexDirection: 'column',
        fontFamily: 'Roboto, system-ui, sans-serif',
        overflowY: 'auto',
      }}
    >
      {/* Hero — layered gradient with bokeh circles */}
      <div
        style={{
          background: isDark
            ? 'linear-gradient(160deg, #071440 0%, #0D2B6B 50%, #0A3080 100%)'
            : 'linear-gradient(160deg, #0D3FA3 0%, #1565C0 55%, #0288D1 100%)',
          padding: '40px 28px 52px',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: 16,
          position: 'relative',
          overflow: 'hidden',
        }}
      >
        {/* Bokeh circle 1 — top-left */}
        <div
          style={{
            position: 'absolute',
            top: -40,
            left: -40,
            width: 200,
            height: 200,
            borderRadius: '50%',
            background: 'rgba(255,255,255,0.07)',
            filter: 'blur(24px)',
            pointerEvents: 'none',
          }}
        />
        {/* Bokeh circle 2 — bottom-right */}
        <div
          style={{
            position: 'absolute',
            bottom: -20,
            right: -30,
            width: 160,
            height: 160,
            borderRadius: '50%',
            background: 'rgba(135,206,250,0.1)',
            filter: 'blur(20px)',
            pointerEvents: 'none',
          }}
        />
        {/* Bokeh circle 3 — center accent */}
        <div
          style={{
            position: 'absolute',
            top: 20,
            right: 60,
            width: 80,
            height: 80,
            borderRadius: '50%',
            background: 'rgba(255,255,255,0.05)',
            filter: 'blur(16px)',
            pointerEvents: 'none',
          }}
        />

        {/* Logo */}
        <div
          style={{
            width: 80,
            height: 80,
            background: 'rgba(255,255,255,0.18)',
            borderRadius: 24,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            border: '1.5px solid rgba(255,255,255,0.35)',
            backdropFilter: 'blur(16px)',
            WebkitBackdropFilter: 'blur(16px)',
            boxShadow: '0 8px 32px rgba(0,0,0,0.18)',
            position: 'relative',
            zIndex: 1,
          }}
        >
          <GraduationCap size={40} color="#fff" strokeWidth={1.4} />
        </div>

        {/* Title + amber underline */}
        <div style={{ textAlign: 'center', position: 'relative', zIndex: 1 }}>
          <div style={{ color: '#fff', fontSize: 24, fontWeight: 700, letterSpacing: '-0.4px', lineHeight: 1.2 }}>
            Campus Found
          </div>
          {/* Amber brand underline */}
          <div
            style={{
              width: 40,
              height: 3,
              background: 'linear-gradient(90deg, #F59E0B, #FBBF24)',
              borderRadius: 2,
              margin: '6px auto 6px',
            }}
          />
          <div style={{ color: 'rgba(255,255,255,0.6)', fontSize: 12, marginTop: 2 }}>
            Lost & Found · Royal University of Phnom Penh
          </div>
        </div>

        {/* App purpose chips */}
        <div style={{ display: 'flex', gap: 8, position: 'relative', zIndex: 1 }}>
          {[
            { icon: <KeyRound size={12} />, label: 'Keys' },
            { icon: <CreditCard size={12} />, label: 'ID Cards' },
            { icon: <Smartphone size={12} />, label: 'Devices' },
          ].map(({ icon, label }) => (
            <div
              key={label}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 5,
                background: 'rgba(255,255,255,0.13)',
                border: '1px solid rgba(255,255,255,0.2)',
                borderRadius: 20,
                padding: '4px 10px',
                color: 'rgba(255,255,255,0.85)',
                fontSize: 11,
                fontWeight: 500,
                backdropFilter: 'blur(8px)',
              }}
            >
              {icon}
              {label}
            </div>
          ))}
        </div>
      </div>

      {/* Form card — curved top, elevated */}
      <div
        style={{
          flex: 1,
          background: surface,
          borderRadius: '20px 20px 0 0',
          marginTop: -20,
          padding: '24px 20px 28px',
          boxShadow: '0 -8px 32px rgba(0,0,0,0.1)',
          position: 'relative',
          zIndex: 2,
        }}
      >
        {/* Drag handle */}
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 20 }}>
          <div style={{ width: 36, height: 4, borderRadius: 2, background: isDark ? '#3A3D45' : '#E5E7EB' }} />
        </div>

        {/* Tab switcher */}
        <div
          style={{
            display: 'flex',
            background: tabBg,
            borderRadius: 10,
            padding: 3,
            marginBottom: 20,
          }}
        >
          {(['login', 'register'] as const).map(t => (
            <button
              key={t}
              onClick={() => setTab(t)}
              style={{
                flex: 1,
                padding: '9px',
                borderRadius: 8,
                border: 'none',
                cursor: 'pointer',
                fontSize: 13,
                fontWeight: tab === t ? 600 : 400,
                color: tab === t ? activeTabText : muted,
                background: tab === t ? activeTabBg : 'transparent',
                transition: 'all 0.2s',
                fontFamily: 'Roboto, system-ui, sans-serif',
                boxShadow: tab === t ? '0 1px 4px rgba(0,0,0,0.14)' : 'none',
              }}
            >
              {t === 'login' ? 'Sign In' : 'Register'}
            </button>
          ))}
        </div>

        {/* Fields */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {tab === 'register' && (
            <Field
              label="Full Name"
              value={name}
              onChange={setName}
              isDark={isDark}
              icon={<User size={16} />}
            />
          )}

          <Field
            label="Student Email"
            value={email}
            onChange={setEmail}
            type="email"
            isDark={isDark}
            icon={<Mail size={16} />}
          />

          <Field
            label="Password"
            value={password}
            onChange={setPassword}
            type={showPass ? 'text' : 'password'}
            isDark={isDark}
            icon={<Lock size={16} />}
            end={
              <button
                onClick={() => setShowPass(p => !p)}
                style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0, color: muted, display: 'flex' }}
              >
                {showPass ? <EyeOff size={15} /> : <Eye size={15} />}
              </button>
            }
          />

          {tab === 'register' && (
            <Field
              label="Confirm Password"
              value={confirm}
              onChange={setConfirm}
              type={showConfirm ? 'text' : 'password'}
              isDark={isDark}
              icon={<Lock size={16} />}
              end={
                <button
                  onClick={() => setShowConfirm(p => !p)}
                  style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0, color: muted, display: 'flex' }}
                >
                  {showConfirm ? <EyeOff size={15} /> : <Eye size={15} />}
                </button>
              }
            />
          )}

          {tab === 'login' && (
            <div style={{ textAlign: 'right', marginTop: -2 }}>
              <span style={{ fontSize: 12, color: primary, cursor: 'pointer', fontWeight: 500 }}>
                Forgot password?
              </span>
            </div>
          )}

          <button
            onClick={onLogin}
            style={{
              width: '100%',
              padding: '14px',
              background: `linear-gradient(135deg, ${isDark ? '#4D8FFF' : '#1565C0'} 0%, ${isDark ? '#7EAAFF' : '#1E88E5'} 100%)`,
              color: '#fff',
              border: 'none',
              borderRadius: 10,
              fontSize: 15,
              fontWeight: 600,
              cursor: 'pointer',
              fontFamily: 'Roboto, system-ui, sans-serif',
              marginTop: 4,
              letterSpacing: '0.02em',
              boxShadow: isDark
                ? '0 4px 20px rgba(126,170,255,0.3)'
                : '0 4px 20px rgba(21,101,192,0.35)',
            }}
          >
            {tab === 'login' ? 'Sign In' : 'Create Account'}
          </button>
        </div>

        {/* Divider */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, margin: '18px 0' }}>
          <div style={{ flex: 1, height: 1, background: border }} />
          <span style={{ fontSize: 11, color: muted, whiteSpace: 'nowrap', textTransform: 'uppercase', letterSpacing: '0.08em' }}>or</span>
          <div style={{ flex: 1, height: 1, background: border }} />
        </div>

        <button
          onClick={onLogin}
          style={{
            width: '100%',
            padding: '13px',
            background: 'transparent',
            color: primary,
            border: `1.5px solid ${isDark ? '#2E4070' : '#C2D4F0'}`,
            borderRadius: 10,
            fontSize: 14,
            fontWeight: 500,
            cursor: 'pointer',
            fontFamily: 'Roboto, system-ui, sans-serif',
          }}
        >
          Browse as Guest
        </button>

        <p style={{ textAlign: 'center', fontSize: 11, color: muted, marginTop: 18, lineHeight: 1.6 }}>
          By continuing, you agree to RUPP's Terms of Service
        </p>
      </div>
    </div>
  );
}
