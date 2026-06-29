import { useState } from 'react';
import type { ElementType } from 'react';
import {
  Laptop, CreditCard, Wallet, Key, BookOpen, Shirt, Package, MapPin, Clock,
} from 'lucide-react';
import type { Item } from '../data/mockData';
import { StatusBadge } from './StatusBadge';

interface Props {
  item: Item;
  isDark: boolean;
  onClick: () => void;
}

const CATEGORY_CONFIG: Record<string, { Icon: ElementType; lightBg: string; darkBg: string; lightIcon: string; darkIcon: string }> = {
  Electronics: { Icon: Laptop,     lightBg: '#EFF6FF', darkBg: '#0F2240', lightIcon: '#2563EB', darkIcon: '#7EAAFF' },
  'Student ID': { Icon: CreditCard, lightBg: '#F0FDF4', darkBg: '#052E16', lightIcon: '#16A34A', darkIcon: '#86EFAC' },
  Wallet:       { Icon: Wallet,     lightBg: '#FFFBEB', darkBg: '#2D1B00', lightIcon: '#D97706', darkIcon: '#FBD96B' },
  Keys:         { Icon: Key,        lightBg: '#FDF4FF', darkBg: '#2E1065', lightIcon: '#9333EA', darkIcon: '#C4B5FD' },
  Books:        { Icon: BookOpen,   lightBg: '#FFF7ED', darkBg: '#2D1200', lightIcon: '#EA580C', darkIcon: '#FDC97E' },
  Clothing:     { Icon: Shirt,      lightBg: '#FDF2F8', darkBg: '#2D0D1F', lightIcon: '#EC4899', darkIcon: '#F9A8D4' },
  Other:        { Icon: Package,    lightBg: '#F9FAFB', darkBg: '#1F2128', lightIcon: '#6B7280', darkIcon: '#9EA3AE' },
};

const STATUS_ACCENT: Record<string, string> = {
  Lost: '#EF4444',
  Found: '#22C55E',
  Claimed: '#A855F7',
};

export function ItemCard({ item, isDark, onClick }: Props) {
  const [hovered, setHovered] = useState(false);

  const surface = isDark ? '#1E1E2A' : '#FFFFFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const border = isDark ? '#2A2D35' : '#EAECF2';

  const cfg = CATEGORY_CONFIG[item.category] ?? CATEGORY_CONFIG.Other;
  const { Icon } = cfg;
  const imgBg = isDark ? cfg.darkBg : cfg.lightBg;
  const iconColor = isDark ? cfg.darkIcon : cfg.lightIcon;
  const accentColor = STATUS_ACCENT[item.status] ?? '#6B7280';

  return (
    <div
      role="button"
      tabIndex={0}
      onClick={onClick}
      onKeyDown={e => e.key === 'Enter' && onClick()}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        background: surface,
        borderRadius: 12,
        borderTop: `1px solid ${border}`,
        borderRight: `1px solid ${border}`,
        borderBottom: `1px solid ${border}`,
        borderLeft: `3px solid ${accentColor}`,
        padding: '10px 12px 10px 10px',
        display: 'flex',
        gap: 10,
        cursor: 'pointer',
        boxShadow: hovered
          ? (isDark
              ? '0 6px 20px rgba(0,0,0,0.45)'
              : '0 6px 20px rgba(0,0,0,0.1), 0 2px 6px rgba(0,0,0,0.06)')
          : (isDark
              ? '0 1px 4px rgba(0,0,0,0.3)'
              : '0 2px 8px rgba(0,0,0,0.06), 0 0 0 1px rgba(0,0,0,0.03)'),
        transform: hovered ? 'translateY(-1px)' : 'translateY(0)',
        transition: 'box-shadow 0.18s, transform 0.18s',
        outline: 'none',
      }}
    >
      {/* Thumbnail */}
      <div
        style={{
          width: 64,
          height: 64,
          borderRadius: 8,
          background: imgBg,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          flexShrink: 0,
          position: 'relative',
          overflow: 'hidden',
        }}
      >
        <Icon size={26} color={iconColor} strokeWidth={1.5} />
        <div
          style={{
            position: 'absolute',
            inset: 0,
            background: 'linear-gradient(135deg, rgba(255,255,255,0.1) 0%, transparent 60%)',
            borderRadius: 8,
            pointerEvents: 'none',
          }}
        />
      </div>

      {/* Content — full width, no overflow */}
      <div style={{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', justifyContent: 'center', gap: 3 }}>

        {/* Row 1: title + badge */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 6, minWidth: 0 }}>
          <span
            style={{
              fontSize: 13,
              fontWeight: 600,
              color: onSurface,
              fontFamily: 'Roboto, system-ui, sans-serif',
              lineHeight: '1.35',
              flex: 1,
              minWidth: 0,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
            }}
          >
            {item.title}
          </span>
          {/* Badge is flex-shrink 0, so title truncates before badge */}
          <div style={{ flexShrink: 0 }}>
            <StatusBadge status={item.status} isDark={isDark} />
          </div>
        </div>

        {/* Row 2: category · location — both on one line, plain text */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 3, minWidth: 0 }}>
          <span
            style={{
              fontSize: 11,
              fontWeight: 500,
              color: iconColor,
              fontFamily: 'Roboto, system-ui, sans-serif',
              flexShrink: 0,
            }}
          >
            {item.category}
          </span>
          <span style={{ color: muted, fontSize: 10, flexShrink: 0, lineHeight: 1 }}>·</span>
          <MapPin size={9} color={muted} style={{ flexShrink: 0 }} />
          <span
            style={{
              fontSize: 11,
              color: muted,
              fontFamily: 'Roboto, system-ui, sans-serif',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              flex: 1,
              minWidth: 0,
            }}
          >
            {item.location.split(',')[0]}
          </span>
        </div>

        {/* Row 3: date & time */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 3 }}>
          <Clock size={9} color={muted} style={{ flexShrink: 0 }} />
          <span
            style={{
              fontSize: 11,
              color: muted,
              fontFamily: 'Roboto, system-ui, sans-serif',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
            }}
          >
            {item.date} · {item.time}
          </span>
        </div>
      </div>
    </div>
  );
}
