import type { ElementType, ReactNode } from 'react';
import {
  ArrowLeft, MapPin, Phone, ExternalLink, Calendar, Clock, Flag,
  Laptop, CreditCard, Wallet, Key, BookOpen, Shirt, Package, CheckCircle2,
  MessageCircle,
} from 'lucide-react';
import { mockItems } from '../../data/mockData';
import { StatusBadge } from '../StatusBadge';
import type { AppProps } from '../../App';

const CATEGORY_CONFIG: Record<string, { Icon: ElementType; lightBg: string; darkBg: string; lightIcon: string; darkIcon: string; accentColor: string }> = {
  Electronics: { Icon: Laptop,     lightBg: '#EFF6FF', darkBg: '#0F2240', lightIcon: '#2563EB', darkIcon: '#7EAAFF', accentColor: '#3B82F6' },
  'Student ID': { Icon: CreditCard, lightBg: '#F0FDF4', darkBg: '#052E16', lightIcon: '#16A34A', darkIcon: '#86EFAC', accentColor: '#22C55E' },
  Wallet:       { Icon: Wallet,     lightBg: '#FFFBEB', darkBg: '#2D1B00', lightIcon: '#D97706', darkIcon: '#FBD96B', accentColor: '#F59E0B' },
  Keys:         { Icon: Key,        lightBg: '#FDF4FF', darkBg: '#2E1065', lightIcon: '#9333EA', darkIcon: '#C4B5FD', accentColor: '#A855F7' },
  Books:        { Icon: BookOpen,   lightBg: '#FFF7ED', darkBg: '#2D1200', lightIcon: '#EA580C', darkIcon: '#FDC97E', accentColor: '#F97316' },
  Clothing:     { Icon: Shirt,      lightBg: '#FDF2F8', darkBg: '#2D0D1F', lightIcon: '#EC4899', darkIcon: '#F9A8D4', accentColor: '#EC4899' },
  Other:        { Icon: Package,    lightBg: '#F9FAFB', darkBg: '#1F2128', lightIcon: '#6B7280', darkIcon: '#9EA3AE', accentColor: '#6B7280' },
};

interface InfoRowProps { icon: ReactNode; label: string; value: string; isDark: boolean; action?: ReactNode; accentColor?: string }

function InfoRow({ icon, label, value, isDark, action, accentColor }: InfoRowProps) {
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';

  return (
    <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
      <div
        style={{
          color: accentColor ?? muted,
          display: 'flex',
          marginTop: 1,
          flexShrink: 0,
        }}
      >
        {icon}
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 11, color: muted, fontFamily: 'Roboto, system-ui, sans-serif', marginBottom: 2, fontWeight: 500 }}>{label}</div>
        <div style={{ fontSize: 13, color: onSurface, fontFamily: 'Roboto, system-ui, sans-serif', lineHeight: 1.55 }}>{value}</div>
        {action && <div style={{ marginTop: 8 }}>{action}</div>}
      </div>
    </div>
  );
}

interface SectionCardProps {
  isDark: boolean;
  border: string;
  accentColor: string;
  children: ReactNode;
}

function SectionCard({ isDark, border, accentColor, children }: SectionCardProps) {
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
        padding: '14px 16px 14px 13px',
        boxShadow: isDark ? '0 1px 4px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.05)',
      }}
    >
      {children}
    </div>
  );
}

export function DetailScreen({ isDark, navigate, selectedItemId }: AppProps) {
  const item = mockItems.find(i => i.id === selectedItemId) ?? mockItems[0];
  const isOwner = item.reporter.name === 'Flow Tester';

  const bg = isDark ? '#0E1016' : '#F0F4FA';
  const surface = isDark ? '#1A1C25' : '#FFFFFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const onSurfaceVariant = isDark ? '#9EA3AE' : '#44474E';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const primary = isDark ? '#7EAAFF' : '#1565C0';
  const border = isDark ? '#252830' : '#EEF2F8';

  const cfg = CATEGORY_CONFIG[item.category] ?? CATEGORY_CONFIG.Other;
  const { Icon, accentColor } = cfg;
  const imgBg = isDark ? cfg.darkBg : cfg.lightBg;
  const iconColor = isDark ? cfg.darkIcon : cfg.lightIcon;

  const sectionLabel = (text: string) => (
    <div
      style={{
        fontSize: 10,
        fontWeight: 700,
        color: muted,
        letterSpacing: '0.1em',
        textTransform: 'uppercase',
        marginBottom: 10,
      }}
    >
      {text}
    </div>
  );

  return (
    <div style={{ height: '100%', background: bg, display: 'flex', flexDirection: 'column', fontFamily: 'Roboto, system-ui, sans-serif' }}>
      {/* Top bar */}
      <div
        style={{
          background: isDark ? 'rgba(26,28,37,0.94)' : 'rgba(255,255,255,0.93)',
          backdropFilter: 'blur(20px)',
          WebkitBackdropFilter: 'blur(20px)',
          padding: '0 16px',
          height: 56,
          display: 'flex',
          alignItems: 'center',
          gap: 8,
          borderBottom: `1px solid ${border}`,
          flexShrink: 0,
          zIndex: 10,
        }}
      >
        <button
          onClick={() => navigate('home')}
          style={{
            background: isDark ? 'rgba(126,170,255,0.1)' : 'rgba(21,101,192,0.08)',
            border: 'none',
            cursor: 'pointer',
            color: primary,
            display: 'flex',
            padding: 6,
            borderRadius: '50%',
          }}
        >
          <ArrowLeft size={18} />
        </button>
        <span style={{ fontSize: 16, fontWeight: 600, color: onSurface, flex: 1 }}>Item Detail</span>
        <StatusBadge status={item.status} isDark={isDark} />
      </div>

      {/* Scrollable content */}
      <div style={{ flex: 1, overflowY: 'auto' }}>

        {/* Hero image — with gradient overlay */}
        <div
          style={{
            height: 220,
            background: imgBg,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            flexShrink: 0,
            position: 'relative',
            overflow: 'hidden',
          }}
        >
          {/* Background pattern glow */}
          <div
            style={{
              position: 'absolute',
              inset: 0,
              backgroundImage: `radial-gradient(circle at 30% 40%, ${accentColor}22 0%, transparent 60%), radial-gradient(circle at 70% 70%, ${accentColor}18 0%, transparent 50%)`,
              pointerEvents: 'none',
            }}
          />
          <Icon size={80} color={iconColor} strokeWidth={1.2} style={{ position: 'relative', zIndex: 1 }} />
          {/* Bottom gradient overlay */}
          <div
            style={{
              position: 'absolute',
              inset: 0,
              background: 'linear-gradient(to top, rgba(0,0,0,0.5) 0%, rgba(0,0,0,0.1) 40%, transparent 70%)',
              pointerEvents: 'none',
            }}
          />
          {/* Status badge on image */}
          <div style={{ position: 'absolute', bottom: 14, left: 16, zIndex: 2 }}>
            <StatusBadge status={item.status} isDark={isDark} />
          </div>
          {/* Category chip on image */}
          <div
            style={{
              position: 'absolute',
              bottom: 14,
              right: 16,
              background: 'rgba(0,0,0,0.45)',
              backdropFilter: 'blur(8px)',
              border: '1px solid rgba(255,255,255,0.2)',
              borderRadius: 20,
              padding: '3px 10px',
              fontSize: 11,
              color: 'rgba(255,255,255,0.9)',
              fontWeight: 500,
              zIndex: 2,
            }}
          >
            {item.category}
          </div>
        </div>

        {/* Title area */}
        <div
          style={{
            background: surface,
            padding: '16px 20px 14px',
            borderBottom: `1px solid ${border}`,
            boxShadow: isDark ? '0 2px 8px rgba(0,0,0,0.2)' : '0 2px 8px rgba(0,0,0,0.04)',
          }}
        >
          <h2 style={{ fontSize: 20, fontWeight: 700, color: onSurface, margin: 0, lineHeight: 1.3, letterSpacing: '-0.3px' }}>
            {item.title}
          </h2>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginTop: 8 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
              <Calendar size={12} color={muted} />
              <span style={{ fontSize: 12, color: muted }}>{item.date}</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
              <Clock size={12} color={muted} />
              <span style={{ fontSize: 12, color: muted }}>{item.time}</span>
            </div>
          </div>
        </div>

        {/* Info sections */}
        <div style={{ padding: '14px 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>

          {/* Description */}
          <SectionCard isDark={isDark} border={border} accentColor={isDark ? '#4D8FFF' : '#1565C0'}>
            {sectionLabel('Description')}
            <p style={{ fontSize: 13, color: onSurfaceVariant, lineHeight: 1.7, margin: 0, fontFamily: 'Roboto, system-ui, sans-serif' }}>
              {item.description}
            </p>
          </SectionCard>

          {/* Location */}
          <SectionCard isDark={isDark} border={border} accentColor="#22C55E">
            {sectionLabel('Location Found')}
            <InfoRow
              icon={<MapPin size={15} />}
              label="Address"
              value={item.location}
              isDark={isDark}
              accentColor="#22C55E"
              action={
                <button
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: 6,
                    padding: '7px 14px',
                    borderRadius: 8,
                    border: `1.5px solid ${isDark ? '#166534' : '#BBF7D0'}`,
                    background: isDark ? 'rgba(34,197,94,0.08)' : '#F0FDF4',
                    color: isDark ? '#86EFAC' : '#15803D',
                    fontSize: 12,
                    fontWeight: 600,
                    cursor: 'pointer',
                    fontFamily: 'Roboto, system-ui, sans-serif',
                  }}
                >
                  <ExternalLink size={12} />
                  Open in Maps
                </button>
              }
            />
          </SectionCard>

          {/* Contact */}
          <SectionCard isDark={isDark} border={border} accentColor="#F59E0B">
            {sectionLabel('Contact Reporter')}
            <InfoRow
              icon={<Phone size={15} />}
              label="Phone / Social"
              value={item.contact}
              isDark={isDark}
              accentColor="#F59E0B"
            />
            <div style={{ fontSize: 11, color: muted, marginTop: 6, paddingLeft: 25 }}>
              Tap to call, message, or open a link
            </div>
          </SectionCard>

          {/* Reporter */}
          <div
            style={{
              background: surface,
              borderRadius: 12,
              border: `1px solid ${border}`,
              padding: '12px 16px',
              display: 'flex',
              alignItems: 'center',
              gap: 10,
              boxShadow: isDark ? '0 1px 4px rgba(0,0,0,0.3)' : '0 2px 8px rgba(0,0,0,0.05)',
            }}
          >
            {/* Double-ring avatar */}
            <div style={{ position: 'relative', flexShrink: 0 }}>
              <div
                style={{
                  width: 42,
                  height: 42,
                  borderRadius: '50%',
                  background: isDark ? '#0D2B6B' : '#DBEAFE',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  border: `2px solid ${isDark ? '#2E4D9A' : '#93C5FD'}`,
                  boxShadow: `0 0 0 3px ${surface}`,
                  outline: `2px solid ${isDark ? 'rgba(126,170,255,0.3)' : 'rgba(21,101,192,0.15)'}`,
                }}
              >
                <span style={{ fontSize: 14, fontWeight: 700, color: primary }}>{item.reporter.initials}</span>
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 10, color: muted, fontWeight: 600, letterSpacing: '0.06em', textTransform: 'uppercase' }}>Reported By</div>
              <div style={{ fontSize: 14, fontWeight: 600, color: onSurface, marginTop: 1 }}>{item.reporter.name}</div>
            </div>
            {isOwner && (
              <div
                style={{
                  fontSize: 11,
                  color: primary,
                  fontWeight: 600,
                  background: isDark ? 'rgba(126,170,255,0.12)' : '#DBEAFE',
                  borderRadius: 6,
                  padding: '3px 10px',
                }}
              >
                You
              </div>
            )}
          </div>

          {/* Owner actions */}
          {isOwner && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8, paddingTop: 4 }}>
              <button
                style={{
                  width: '100%',
                  padding: '13px',
                  background: isDark ? 'rgba(34,197,94,0.1)' : '#DCFCE7',
                  color: isDark ? '#86EFAC' : '#15803D',
                  border: `1.5px solid ${isDark ? '#166534' : '#BBF7D0'}`,
                  borderRadius: 10,
                  fontSize: 14,
                  fontWeight: 600,
                  cursor: 'pointer',
                  fontFamily: 'Roboto, system-ui, sans-serif',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: 8,
                }}
              >
                <CheckCircle2 size={16} />
                Mark as Claimed
              </button>
              <button
                style={{
                  width: '100%',
                  padding: '12px',
                  background: 'transparent',
                  color: isDark ? '#FCA5A5' : '#C62828',
                  border: `1.5px solid ${isDark ? '#7F1D1D' : '#FECACA'}`,
                  borderRadius: 10,
                  fontSize: 14,
                  fontWeight: 500,
                  cursor: 'pointer',
                  fontFamily: 'Roboto, system-ui, sans-serif',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: 8,
                }}
              >
                <Flag size={14} />
                Delete Post
              </button>
            </div>
          )}

          {/* Non-owner CTA — amber */}
          {!isOwner && (
            <button
              style={{
                width: '100%',
                padding: '14px',
                background: isDark
                  ? 'linear-gradient(135deg, #D97706 0%, #F59E0B 100%)'
                  : 'linear-gradient(135deg, #D97706 0%, #F59E0B 100%)',
                color: '#fff',
                border: 'none',
                borderRadius: 10,
                fontSize: 14,
                fontWeight: 700,
                cursor: 'pointer',
                fontFamily: 'Roboto, system-ui, sans-serif',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 8,
                boxShadow: '0 4px 20px rgba(217,119,6,0.35)',
                letterSpacing: '0.01em',
              }}
            >
              <MessageCircle size={16} />
              Contact Reporter
            </button>
          )}

          <div style={{ height: 8 }} />
        </div>
      </div>
    </div>
  );
}
