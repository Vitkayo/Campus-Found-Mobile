import { useState, type ReactNode, type CSSProperties } from 'react';
import {
  ImageIcon, Camera, ChevronDown, MapPin, Navigation, Calendar,
  Phone, CheckCircle2, ArrowLeft, Sparkles,
} from 'lucide-react';
import { CATEGORIES, LOCATIONS } from '../../data/mockData';
import { BottomNav } from '../BottomNav';
import type { AppProps } from '../../App';

// Section card with colored left accent border
interface SectionCardProps {
  isDark: boolean;
  accentColor: string;
  children: ReactNode;
  border: string;
}

function SectionCard({ isDark, accentColor, children, border }: SectionCardProps) {
  const surface = isDark ? '#1A1C25' : '#FFFFFF';
  return (
    <div
      style={{
        background: surface,
        borderRadius: 12,
        borderTop: `1px solid ${border}`,
        borderRight: `1px solid ${border}`,
        borderBottom: `1px solid ${border}`,
        borderLeft: `3px solid ${accentColor}`,
        padding: '14px 15px 14px 13px',
        marginBottom: 10,
        boxShadow: isDark ? '0 1px 4px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.05)',
      }}
    >
      {children}
    </div>
  );
}

interface SectionLabelProps { label: string; isDark: boolean; accentColor: string }

function SectionLabel({ label, isDark, accentColor }: SectionLabelProps) {
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  return (
    <div
      style={{
        fontSize: 10,
        fontWeight: 700,
        color: accentColor,
        letterSpacing: '0.1em',
        textTransform: 'uppercase',
        marginBottom: 12,
        display: 'flex',
        alignItems: 'center',
        gap: 5,
      }}
    >
      <div style={{ width: 3, height: 10, background: accentColor, borderRadius: 2 }} />
      {label}
    </div>
  );
}

interface InputProps {
  label: string;
  value: string;
  onChange: (v: string) => void;
  isDark: boolean;
  multiline?: boolean;
  rows?: number;
  icon?: ReactNode;
  hint?: string;
}

function OutlinedField({ label, value, onChange, isDark, multiline, rows = 3, icon, hint }: InputProps) {
  const [focused, setFocused] = useState(false);
  const border = focused ? (isDark ? '#7EAAFF' : '#1565C0') : (isDark ? '#3A3D45' : '#D1D5DB');
  const bg = isDark ? '#22252F' : '#FAFBFF';
  const focusBg = focused ? (isDark ? 'rgba(126,170,255,0.05)' : 'rgba(21,101,192,0.02)') : bg;
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const labelColor = focused ? (isDark ? '#7EAAFF' : '#1565C0') : muted;

  const sharedStyle: CSSProperties = {
    flex: 1,
    background: 'none',
    border: 'none',
    outline: 'none',
    fontSize: 13,
    color: onSurface,
    fontFamily: 'Roboto, system-ui, sans-serif',
    padding: 0,
    resize: 'none' as const,
  };

  return (
    <div>
      <div
        style={{
          background: focusBg,
          border: `1.5px solid ${border}`,
          borderRadius: 8,
          padding: multiline ? '10px 12px' : '0 12px',
          transition: 'border-color 0.18s, background 0.18s, box-shadow 0.18s',
          minHeight: multiline ? undefined : 48,
          display: multiline ? 'block' : 'flex',
          alignItems: 'center',
          gap: 8,
          boxShadow: focused ? (isDark ? '0 0 0 3px rgba(126,170,255,0.1)' : '0 0 0 3px rgba(21,101,192,0.08)') : 'none',
        }}
      >
        {!multiline && icon && <span style={{ color: focused ? (isDark ? '#7EAAFF' : '#1565C0') : muted, display: 'flex', flexShrink: 0, transition: 'color 0.18s' }}>{icon}</span>}
        {multiline ? (
          <div>
            <div style={{ fontSize: 10, color: labelColor, marginBottom: 3, fontFamily: 'Roboto, system-ui, sans-serif', fontWeight: 600, letterSpacing: '0.04em', textTransform: 'uppercase', transition: 'color 0.18s' }}>{label}</div>
            <textarea
              value={value}
              onChange={e => onChange(e.target.value)}
              rows={rows}
              onFocus={() => setFocused(true)}
              onBlur={() => setFocused(false)}
              style={{ ...sharedStyle, width: '100%', display: 'block' }}
              placeholder="Add details..."
            />
          </div>
        ) : (
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 10, color: labelColor, fontFamily: 'Roboto, system-ui, sans-serif', marginBottom: 1, fontWeight: 600, letterSpacing: '0.04em', textTransform: 'uppercase', transition: 'color 0.18s' }}>{label}</div>
            <input
              value={value}
              onChange={e => onChange(e.target.value)}
              onFocus={() => setFocused(true)}
              onBlur={() => setFocused(false)}
              style={sharedStyle}
            />
          </div>
        )}
      </div>
      {hint && (
        <div style={{ fontSize: 11, color: isDark ? '#5A5F70' : '#9CA3AF', marginTop: 4, paddingLeft: 2 }}>
          {hint}
        </div>
      )}
    </div>
  );
}

interface SelectProps {
  label: string;
  value: string;
  onChange: (v: string) => void;
  options: string[];
  isDark: boolean;
  icon?: ReactNode;
}

function SelectField({ label, value, onChange, options, isDark, icon }: SelectProps) {
  const border = isDark ? '#3A3D45' : '#D1D5DB';
  const bg = isDark ? '#22252F' : '#FAFBFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';

  return (
    <div
      style={{
        background: bg,
        border: `1.5px solid ${border}`,
        borderRadius: 8,
        padding: '0 12px',
        minHeight: 48,
        display: 'flex',
        alignItems: 'center',
        gap: 8,
      }}
    >
      {icon && <span style={{ color: muted, display: 'flex', flexShrink: 0 }}>{icon}</span>}
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 10, color: muted, fontFamily: 'Roboto, system-ui, sans-serif', marginBottom: 1, fontWeight: 600, letterSpacing: '0.04em', textTransform: 'uppercase' }}>{label}</div>
        <select
          value={value}
          onChange={e => onChange(e.target.value)}
          style={{
            width: '100%',
            background: 'none',
            border: 'none',
            outline: 'none',
            fontSize: 13,
            color: value ? onSurface : muted,
            fontFamily: 'Roboto, system-ui, sans-serif',
            padding: 0,
            cursor: 'pointer',
            appearance: 'none',
          }}
        >
          <option value="">Select {label.toLowerCase()}...</option>
          {options.map(o => <option key={o} value={o}>{o}</option>)}
        </select>
      </div>
      <ChevronDown size={14} color={muted} style={{ flexShrink: 0, pointerEvents: 'none' }} />
    </div>
  );
}

// Step progress dots
function StepDots({ current, total, isDark }: { current: number; total: number; isDark: boolean }) {
  const primary = isDark ? '#7EAAFF' : '#1565C0';
  const inactive = isDark ? '#3A3D45' : '#D1D5DB';
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
      {Array.from({ length: total }).map((_, i) => (
        <div
          key={i}
          style={{
            width: i < current ? 20 : 6,
            height: 6,
            borderRadius: 3,
            background: i < current ? primary : inactive,
            transition: 'width 0.25s, background 0.25s',
          }}
        />
      ))}
    </div>
  );
}

export function ReportScreen({ isDark, navigate }: AppProps) {
  const [mode, setMode] = useState<'Lost' | 'Found'>('Lost');
  const [submitted, setSubmitted] = useState(false);
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('');
  const [description, setDescription] = useState('');
  const [location, setLocation] = useState('');
  const [contact, setContact] = useState('');
  const [date, setDate] = useState('');
  const [hasImage, setHasImage] = useState(false);

  const bg = isDark ? '#0E1016' : '#F0F4FA';
  const surface = isDark ? '#1A1C25' : '#FFFFFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const primary = isDark ? '#7EAAFF' : '#1565C0';
  const border = isDark ? '#252830' : '#EEF2F8';

  // Count filled fields for progress dots
  const filled = [title, category, description, location, contact].filter(Boolean).length;
  const progress = Math.min(5, filled + (hasImage ? 1 : 0));

  if (submitted) {
    return (
      <div style={{ height: '100%', background: bg, display: 'flex', flexDirection: 'column', fontFamily: 'Roboto, system-ui, sans-serif' }}>
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 18, padding: 32 }}>
          <div
            style={{
              width: 80,
              height: 80,
              borderRadius: '50%',
              background: isDark ? 'rgba(34,197,94,0.1)' : '#DCFCE7',
              border: `2px solid ${isDark ? '#166534' : '#BBF7D0'}`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: '0 0 0 8px ' + (isDark ? 'rgba(34,197,94,0.06)' : 'rgba(34,197,94,0.08)'),
            }}
          >
            <CheckCircle2 size={38} color={isDark ? '#86EFAC' : '#16A34A'} />
          </div>
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 20, fontWeight: 700, color: onSurface }}>Post Submitted!</div>
            <div style={{ fontSize: 13, color: muted, marginTop: 8, lineHeight: 1.7 }}>
              Your item has been reported.<br />We'll notify you when someone responds.
            </div>
          </div>
          <button
            onClick={() => { setSubmitted(false); navigate('home'); }}
            style={{
              padding: '12px 36px',
              background: isDark ? 'linear-gradient(135deg, #4D8FFF, #7EAAFF)' : 'linear-gradient(135deg, #1565C0, #1E88E5)',
              color: '#fff',
              border: 'none',
              borderRadius: 10,
              fontSize: 14,
              fontWeight: 600,
              cursor: 'pointer',
              fontFamily: 'Roboto, system-ui, sans-serif',
              boxShadow: isDark ? '0 4px 16px rgba(126,170,255,0.3)' : '0 4px 16px rgba(21,101,192,0.35)',
            }}
          >
            Back to Home
          </button>
        </div>
        <BottomNav active="report" isDark={isDark} navigate={navigate} />
      </div>
    );
  }

  return (
    <div style={{ height: '100%', background: bg, display: 'flex', flexDirection: 'column', fontFamily: 'Roboto, system-ui, sans-serif' }}>
      {/* App Bar */}
      <div
        style={{
          background: isDark ? 'rgba(26,28,37,0.94)' : 'rgba(255,255,255,0.93)',
          backdropFilter: 'blur(20px)',
          WebkitBackdropFilter: 'blur(20px)',
          padding: '0 16px',
          height: 56,
          display: 'flex',
          alignItems: 'center',
          gap: 10,
          borderBottom: `1px solid ${border}`,
          flexShrink: 0,
          zIndex: 10,
        }}
      >
        <button
          onClick={() => navigate('home')}
          style={{
            background: isDark ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.04)',
            border: 'none',
            cursor: 'pointer',
            color: muted,
            display: 'flex',
            padding: 6,
            borderRadius: '50%',
          }}
        >
          <ArrowLeft size={18} />
        </button>
        <span style={{ fontSize: 17, fontWeight: 700, color: onSurface, flex: 1 }}>Report Item</span>
        {/* Progress indicator */}
        <StepDots current={progress} total={5} isDark={isDark} />
      </div>

      {/* Scrollable form */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '14px 16px 8px' }}>

        {/* Lost / Found selector */}
        <SectionCard isDark={isDark} accentColor={isDark ? '#7EAAFF' : '#1565C0'} border={border}>
          <SectionLabel label="Item Status" isDark={isDark} accentColor={isDark ? '#7EAAFF' : '#1565C0'} />
          <div
            style={{
              display: 'flex',
              background: isDark ? '#22252F' : '#F0F4F8',
              borderRadius: 8,
              padding: 3,
              gap: 3,
            }}
          >
            {(['Lost', 'Found'] as const).map(m => (
              <button
                key={m}
                onClick={() => setMode(m)}
                style={{
                  flex: 1,
                  padding: '9px',
                  borderRadius: 6,
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: 13,
                  fontWeight: mode === m ? 700 : 400,
                  color: mode === m
                    ? (m === 'Lost' ? '#C62828' : '#15803D')
                    : muted,
                  background: mode === m
                    ? (isDark ? (m === 'Lost' ? '#3B1212' : '#052E16') : (m === 'Lost' ? '#FEE2E2' : '#DCFCE7'))
                    : 'transparent',
                  fontFamily: 'Roboto, system-ui, sans-serif',
                  transition: 'all 0.2s',
                  boxShadow: mode === m ? '0 1px 4px rgba(0,0,0,0.14)' : 'none',
                }}
              >
                {m}
              </button>
            ))}
          </div>
        </SectionCard>

        {/* Photo section */}
        <SectionCard isDark={isDark} accentColor="#F59E0B" border={border}>
          <SectionLabel label="Item Photo" isDark={isDark} accentColor="#F59E0B" />
          <div
            onClick={() => setHasImage(true)}
            style={{
              background: hasImage
                ? (isDark ? '#1A3050' : '#EBF4FF')
                : (isDark ? '#22252F' : '#EEF5FF'),
              border: `1.5px dashed ${isDark ? 'rgba(126,170,255,0.35)' : 'rgba(21,101,192,0.3)'}`,
              borderRadius: 8,
              height: 100,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 6,
              cursor: 'pointer',
              marginBottom: 10,
              position: 'relative',
              overflow: 'hidden',
              backgroundImage: hasImage
                ? 'none'
                : `repeating-linear-gradient(
                    -45deg,
                    transparent,
                    transparent 8px,
                    ${isDark ? 'rgba(126,170,255,0.03)' : 'rgba(21,101,192,0.03)'} 8px,
                    ${isDark ? 'rgba(126,170,255,0.03)' : 'rgba(21,101,192,0.03)'} 16px
                  )`,
            }}
          >
            {hasImage ? (
              <>
                <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <ImageIcon size={36} color={isDark ? '#7EAAFF' : '#1565C0'} strokeWidth={1.2} />
                </div>
                <div
                  style={{
                    position: 'absolute',
                    bottom: 6,
                    right: 8,
                    background: isDark ? '#1A1C25' : '#fff',
                    borderRadius: 6,
                    padding: '2px 8px',
                    fontSize: 11,
                    color: isDark ? '#7EAAFF' : '#1565C0',
                    fontWeight: 600,
                    boxShadow: '0 1px 4px rgba(0,0,0,0.12)',
                  }}
                >
                  Tap to change
                </div>
              </>
            ) : (
              <>
                <Sparkles size={22} color={isDark ? '#7EAAFF' : '#1565C0'} strokeWidth={1.4} />
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: 13, fontWeight: 600, color: isDark ? '#7EAAFF' : '#1565C0' }}>Add a photo</div>
                  <div style={{ fontSize: 11, color: muted, marginTop: 1 }}>JPEG or PNG · up to 5 MB</div>
                </div>
              </>
            )}
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            {[
              { icon: <ImageIcon size={13} />, label: 'Choose Photo' },
              { icon: <Camera size={13} />, label: 'Take Photo' },
            ].map(({ icon, label }) => (
              <button
                key={label}
                onClick={() => setHasImage(true)}
                style={{
                  flex: 1,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: 6,
                  padding: '8px',
                  borderRadius: 8,
                  border: `1.5px solid ${isDark ? 'rgba(245,158,11,0.35)' : 'rgba(245,158,11,0.4)'}`,
                  background: isDark ? 'rgba(245,158,11,0.07)' : 'rgba(245,158,11,0.05)',
                  color: isDark ? '#FBD96B' : '#B45309',
                  fontSize: 12,
                  fontWeight: 600,
                  cursor: 'pointer',
                  fontFamily: 'Roboto, system-ui, sans-serif',
                }}
              >
                {icon}
                {label}
              </button>
            ))}
          </div>
        </SectionCard>

        {/* Item details */}
        <SectionCard isDark={isDark} accentColor="#A855F7" border={border}>
          <SectionLabel label="Item Details" isDark={isDark} accentColor="#A855F7" />
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <OutlinedField label="Title" value={title} onChange={setTitle} isDark={isDark} />
            <SelectField label="Category" value={category} onChange={setCategory} options={CATEGORIES} isDark={isDark} />
            <OutlinedField label="Description" value={description} onChange={setDescription} isDark={isDark} multiline rows={3} hint="Describe color, brand, distinguishing features..." />
          </div>
        </SectionCard>

        {/* Location */}
        <SectionCard isDark={isDark} accentColor="#22C55E" border={border}>
          <SectionLabel label="Location" isDark={isDark} accentColor="#22C55E" />
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <SelectField label="Last Seen Location" value={location} onChange={setLocation} options={LOCATIONS} isDark={isDark} icon={<MapPin size={14} />} />
            <div style={{ fontSize: 11, color: muted, marginTop: -4 }}>Pick a RUPP spot or type your own</div>
            <button
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 6,
                padding: '10px',
                borderRadius: 8,
                border: `1.5px solid ${isDark ? 'rgba(34,197,94,0.35)' : 'rgba(34,197,94,0.4)'}`,
                background: isDark ? 'rgba(34,197,94,0.07)' : 'rgba(34,197,94,0.05)',
                color: isDark ? '#86EFAC' : '#15803D',
                fontSize: 13,
                fontWeight: 600,
                cursor: 'pointer',
                fontFamily: 'Roboto, system-ui, sans-serif',
                width: '100%',
              }}
            >
              <Navigation size={14} />
              Use My Current Location
            </button>
          </div>
        </SectionCard>

        {/* Contact & date */}
        <SectionCard isDark={isDark} accentColor="#F97316" border={border}>
          <SectionLabel label="Contact & Date" isDark={isDark} accentColor="#F97316" />
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <OutlinedField
              label="Contact"
              value={contact}
              onChange={setContact}
              isDark={isDark}
              icon={<Phone size={14} />}
              hint="Phone, Telegram, Instagram, or Facebook"
            />
            <OutlinedField
              label="Date of Loss / Discovery"
              value={date}
              onChange={setDate}
              isDark={isDark}
              icon={<Calendar size={14} />}
            />
          </div>
        </SectionCard>

        {/* Submit */}
        <button
          onClick={() => setSubmitted(true)}
          style={{
            width: '100%',
            padding: '15px',
            background: isDark
              ? 'linear-gradient(135deg, #4D8FFF 0%, #7EAAFF 100%)'
              : 'linear-gradient(135deg, #1565C0 0%, #1E88E5 100%)',
            color: '#fff',
            border: 'none',
            borderRadius: 10,
            fontSize: 15,
            fontWeight: 700,
            cursor: 'pointer',
            fontFamily: 'Roboto, system-ui, sans-serif',
            letterSpacing: '0.02em',
            boxShadow: isDark
              ? '0 4px 20px rgba(126,170,255,0.28)'
              : '0 4px 20px rgba(21,101,192,0.35)',
            marginBottom: 12,
          }}
        >
          Submit Post
        </button>
      </div>

      <BottomNav active="report" isDark={isDark} navigate={navigate} />
    </div>
  );
}
