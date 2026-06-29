import { useState } from 'react';
import { Moon, Sun, Pencil, LogOut, PackageSearch } from 'lucide-react';
import { currentUser } from '../../data/mockData';
import { ItemCard } from '../ItemCard';
import { BottomNav } from '../BottomNav';
import { EditProfileDialog } from './EditProfileDialog';
import type { AppProps } from '../../App';

interface StatCardProps {
  value: number;
  label: string;
  color: string;
  bg: string;
  barColor: string;
  maxValue: number;
  isDark: boolean;
}

function StatCard({ value, label, color, bg, barColor, maxValue, isDark }: StatCardProps) {
  const surface = isDark ? '#1A1C25' : '#FFFFFF';
  const border = isDark ? '#252830' : '#EEF2F8';
  const barBg = isDark ? '#2A2D38' : '#F0F4FA';
  const pct = maxValue > 0 ? Math.max(8, Math.round((value / maxValue) * 100)) : 8;

  return (
    <div
      style={{
        flex: 1,
        background: surface,
        borderRadius: 12,
        border: `1px solid ${border}`,
        padding: '12px 10px 10px',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: 2,
        boxShadow: isDark ? '0 1px 4px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.05)',
      }}
    >
      <div
        style={{
          width: 36,
          height: 36,
          borderRadius: '50%',
          background: bg,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          marginBottom: 4,
        }}
      >
        <span style={{ fontSize: 15, fontWeight: 700, color }}>{value}</span>
      </div>
      <span
        style={{
          fontSize: 10,
          fontWeight: 700,
          color: isDark ? '#5A5F70' : '#9CA3AF',
          letterSpacing: '0.08em',
          textAlign: 'center',
          fontFamily: 'Roboto, system-ui, sans-serif',
          textTransform: 'uppercase',
        }}
      >
        {label}
      </span>
      {/* Mini progress bar */}
      <div style={{ width: '100%', height: 4, background: barBg, borderRadius: 2, marginTop: 6, overflow: 'hidden' }}>
        <div
          style={{
            height: '100%',
            width: `${pct}%`,
            background: barColor,
            borderRadius: 2,
            transition: 'width 0.4s ease',
          }}
        />
      </div>
    </div>
  );
}

// Inline SVG dot mesh for banner overlay
const MeshPattern = () => (
  <svg
    style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', opacity: 0.12, pointerEvents: 'none' }}
    xmlns="http://www.w3.org/2000/svg"
  >
    <defs>
      <pattern id="dots" width="16" height="16" patternUnits="userSpaceOnUse">
        <circle cx="2" cy="2" r="1.5" fill="white" />
      </pattern>
    </defs>
    <rect width="100%" height="100%" fill="url(#dots)" />
  </svg>
);

export function ProfileScreen({ isDark, navigate, toggleDark }: AppProps) {
  const [showEdit, setShowEdit] = useState(false);

  const bg = isDark ? '#0E1016' : '#F0F4FA';
  const surface = isDark ? '#1A1C25' : '#FFFFFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const primary = isDark ? '#7EAAFF' : '#1565C0';
  const border = isDark ? '#252830' : '#EEF2F8';

  const totalPosts = currentUser.posts.length;
  const lostCount = currentUser.posts.filter(p => p.status === 'Lost').length;
  const foundCount = currentUser.posts.filter(p => p.status === 'Found').length;

  return (
    <div
      style={{
        height: '100%',
        background: bg,
        display: 'flex',
        flexDirection: 'column',
        fontFamily: 'Roboto, system-ui, sans-serif',
        position: 'relative',
      }}
    >
      {/* Frosted App Bar */}
      <div
        style={{
          background: isDark ? 'rgba(26,28,37,0.94)' : 'rgba(255,255,255,0.93)',
          backdropFilter: 'blur(20px)',
          WebkitBackdropFilter: 'blur(20px)',
          padding: '0 16px',
          height: 56,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          borderBottom: `1px solid ${border}`,
          flexShrink: 0,
          zIndex: 10,
        }}
      >
        <span style={{ fontSize: 17, fontWeight: 700, color: onSurface }}>My Profile</span>
        <button
          onClick={toggleDark}
          style={{
            width: 36,
            height: 36,
            borderRadius: '50%',
            background: isDark ? 'rgba(126,170,255,0.12)' : 'rgba(21,101,192,0.08)',
            border: `1px solid ${isDark ? 'rgba(126,170,255,0.2)' : 'rgba(21,101,192,0.12)'}`,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          {isDark ? <Sun size={16} color={primary} /> : <Moon size={16} color={primary} />}
        </button>
      </div>

      {/* Scrollable content */}
      <div style={{ flex: 1, overflowY: 'auto' }}>

        {/* Profile header card */}
        <div
          style={{
            margin: '14px 16px 0',
            background: surface,
            borderRadius: 16,
            border: `1px solid ${border}`,
            overflow: 'hidden',
            boxShadow: isDark ? '0 4px 16px rgba(0,0,0,0.35)' : '0 4px 16px rgba(0,0,0,0.08)',
          }}
        >
          {/* Gradient banner — taller with mesh */}
          <div
            style={{
              height: 96,
              background: isDark
                ? 'linear-gradient(135deg, #071440 0%, #0D2B6B 50%, #1A3A8A 100%)'
                : 'linear-gradient(135deg, #0D3FA3 0%, #1565C0 55%, #1E88E5 100%)',
              position: 'relative',
              overflow: 'hidden',
            }}
          >
            <MeshPattern />
            {/* Bokeh accent */}
            <div
              style={{
                position: 'absolute',
                top: -20,
                right: -10,
                width: 100,
                height: 100,
                borderRadius: '50%',
                background: 'rgba(255,255,255,0.07)',
                filter: 'blur(16px)',
              }}
            />
            <div
              style={{
                position: 'absolute',
                bottom: -10,
                left: 20,
                width: 70,
                height: 70,
                borderRadius: '50%',
                background: 'rgba(135,206,250,0.08)',
                filter: 'blur(14px)',
              }}
            />
          </div>

          {/* Avatar — overlaps banner bottom */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', padding: '0 16px', marginTop: -30, position: 'relative', zIndex: 2 }}>
            {/* Avatar circle */}
            <div
              style={{
                width: 64,
                height: 64,
                borderRadius: '50%',
                background: isDark ? '#0D2B6B' : '#DBEAFE',
                border: `3px solid ${surface}`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: isDark
                  ? `0 0 0 2px ${primary}40`
                  : `0 0 0 2px ${primary}30`,
                flexShrink: 0,
              }}
            >
              <span style={{ fontSize: 22, fontWeight: 700, color: primary }}>
                {currentUser.initials}
              </span>
            </div>
          </div>

          {/* Name + contact info — safely below the banner */}
          <div style={{ padding: '10px 20px 16px' }}>
            <div style={{ fontSize: 17, fontWeight: 700, color: onSurface, lineHeight: 1.3 }}>
              {currentUser.name}
            </div>
            <div style={{ fontSize: 12, color: muted, marginTop: 2 }}>{currentUser.email}</div>
            <div style={{ fontSize: 12, color: muted }}>{currentUser.phone}</div>
          </div>

          {/* Stats */}
          <div style={{ padding: '0 16px 16px', display: 'flex', gap: 8 }}>
            <StatCard
              value={totalPosts}
              label="Posts"
              color={isDark ? '#7EAAFF' : '#1565C0'}
              bg={isDark ? '#0D2B6B' : '#DBEAFE'}
              barColor={isDark ? '#7EAAFF' : '#1565C0'}
              maxValue={totalPosts || 1}
              isDark={isDark}
            />
            <StatCard
              value={lostCount}
              label="Lost"
              color={isDark ? '#FCA5A5' : '#C62828'}
              bg={isDark ? '#3B1212' : '#FEE2E2'}
              barColor="#EF4444"
              maxValue={totalPosts || 1}
              isDark={isDark}
            />
            <StatCard
              value={foundCount}
              label="Found"
              color={isDark ? '#86EFAC' : '#15803D'}
              bg={isDark ? '#052E16' : '#DCFCE7'}
              barColor="#22C55E"
              maxValue={totalPosts || 1}
              isDark={isDark}
            />
          </div>
        </div>

        {/* Action buttons */}
        <div style={{ margin: '12px 16px 0', display: 'flex', gap: 10 }}>
          <button
            onClick={() => setShowEdit(true)}
            style={{
              flex: 2,
              padding: '12px',
              background: isDark
                ? 'linear-gradient(135deg, #4D8FFF 0%, #7EAAFF 100%)'
                : 'linear-gradient(135deg, #1565C0 0%, #1E88E5 100%)',
              color: '#fff',
              border: 'none',
              borderRadius: 10,
              fontSize: 13,
              fontWeight: 600,
              cursor: 'pointer',
              fontFamily: 'Roboto, system-ui, sans-serif',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 7,
              boxShadow: isDark
                ? '0 4px 16px rgba(126,170,255,0.25)'
                : '0 4px 16px rgba(21,101,192,0.28)',
            }}
          >
            <Pencil size={14} />
            Edit Profile
          </button>
          <button
            style={{
              flex: 1,
              padding: '12px',
              background: 'transparent',
              color: isDark ? '#FCA5A5' : '#C62828',
              border: `1.5px solid ${isDark ? '#7F1D1D' : '#FECACA'}`,
              borderRadius: 10,
              fontSize: 13,
              fontWeight: 500,
              cursor: 'pointer',
              fontFamily: 'Roboto, system-ui, sans-serif',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 6,
            }}
            onClick={() => navigate('login')}
          >
            <LogOut size={14} />
            Logout
          </button>
        </div>

        {/* My Posted Items */}
        <div style={{ margin: '20px 16px 0' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
            <span style={{ fontSize: 14, fontWeight: 700, color: onSurface }}>My Posted Items</span>
            {totalPosts > 0 && (
              <span
                style={{
                  fontSize: 11,
                  fontWeight: 600,
                  color: primary,
                  background: isDark ? 'rgba(126,170,255,0.12)' : '#DBEAFE',
                  borderRadius: 20,
                  padding: '2px 10px',
                }}
              >
                {totalPosts} post{totalPosts !== 1 ? 's' : ''}
              </span>
            )}
          </div>

          {currentUser.posts.length > 0 ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {currentUser.posts.map(item => (
                <ItemCard key={item.id} item={item} isDark={isDark} onClick={() => navigate('detail', item.id)} />
              ))}
            </div>
          ) : (
            <div
              style={{
                background: surface,
                borderRadius: 16,
                border: `1px solid ${border}`,
                padding: '40px 24px',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: 10,
                boxShadow: isDark ? '0 1px 4px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.05)',
              }}
            >
              <div
                style={{
                  width: 56,
                  height: 56,
                  borderRadius: '50%',
                  background: isDark ? '#22252F' : '#EEF2FF',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <PackageSearch size={24} color={isDark ? '#5A5F70' : '#93A8D0'} strokeWidth={1.5} />
              </div>
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 14, fontWeight: 700, color: onSurface }}>No posts yet</div>
                <div style={{ fontSize: 12, color: muted, marginTop: 4, lineHeight: 1.6 }}>
                  Report a lost or found item to get started
                </div>
              </div>
              <button
                onClick={() => navigate('report')}
                style={{
                  padding: '10px 22px',
                  background: isDark ? 'linear-gradient(135deg, #4D8FFF, #7EAAFF)' : 'linear-gradient(135deg, #1565C0, #1E88E5)',
                  color: '#fff',
                  border: 'none',
                  borderRadius: 8,
                  fontSize: 13,
                  fontWeight: 600,
                  cursor: 'pointer',
                  fontFamily: 'Roboto, system-ui, sans-serif',
                  marginTop: 4,
                  boxShadow: isDark ? '0 4px 14px rgba(126,170,255,0.25)' : '0 4px 14px rgba(21,101,192,0.3)',
                }}
              >
                Report Item
              </button>
            </div>
          )}
        </div>

        <div style={{ height: 24 }} />
      </div>

      <BottomNav active="profile" isDark={isDark} navigate={navigate} />

      {showEdit && (
        <EditProfileDialog
          isDark={isDark}
          onClose={() => setShowEdit(false)}
          initialName={currentUser.name}
          initialEmail={currentUser.email}
          initialPhone={currentUser.phone}
          initials={currentUser.initials}
        />
      )}
    </div>
  );
}
