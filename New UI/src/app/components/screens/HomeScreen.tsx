import { useState } from 'react';
import { Search, Moon, Sun, Plus, Sparkles } from 'lucide-react';
import { mockItems } from '../../data/mockData';
import { ItemCard } from '../ItemCard';
import { BottomNav } from '../BottomNav';
import type { AppProps } from '../../App';

const FILTERS = ['All', 'Lost', 'Found', 'Electronics', 'Wallet', 'Student ID', 'Keys', 'Books', 'Other'];

export function HomeScreen({ isDark, navigate, toggleDark }: AppProps) {
  const [activeFilter, setActiveFilter] = useState('All');
  const [search, setSearch] = useState('');

  const bg = isDark ? '#0E1016' : '#F0F4FA';
  const surface = isDark ? '#1A1C25' : '#FFFFFF';
  const onSurface = isDark ? '#E2E3E8' : '#1C1B1F';
  const muted = isDark ? '#5A5F70' : '#9CA3AF';
  const primary = isDark ? '#7EAAFF' : '#1565C0';
  const border = isDark ? '#252830' : '#EEF2F8';
  const searchBg = isDark ? '#22252F' : '#F5F7FA';
  const chipActiveBg = isDark ? '#7EAAFF' : '#1565C0';
  const chipInactiveBorder = isDark ? '#3A3D48' : '#CDD5E0';

  const filtered = mockItems.filter(item => {
    const matchFilter =
      activeFilter === 'All' ||
      item.status === activeFilter ||
      item.category === activeFilter;
    const q = search.toLowerCase();
    const matchSearch =
      !q ||
      item.title.toLowerCase().includes(q) ||
      item.category.toLowerCase().includes(q) ||
      item.location.toLowerCase().includes(q);
    return matchFilter && matchSearch;
  });

  const lostCount = mockItems.filter(i => i.status === 'Lost').length;
  const foundCount = mockItems.filter(i => i.status === 'Found').length;

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
          padding: '10px 16px 10px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          borderBottom: `1px solid ${isDark ? 'rgba(126,170,255,0.08)' : 'rgba(21,101,192,0.07)'}`,
          flexShrink: 0,
          zIndex: 10,
        }}
      >
        <div>
          <div
            style={{
              fontSize: 19,
              fontWeight: 700,
              letterSpacing: '-0.3px',
              lineHeight: 1.3,
              background: isDark
                ? 'linear-gradient(90deg, #7EAAFF 0%, #A8C7FA 100%)'
                : 'linear-gradient(90deg, #0D47A1 0%, #1565C0 60%, #1E88E5 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
            }}
          >
            Campus Found
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginTop: 1 }}>
            <div style={{ width: 6, height: 6, borderRadius: '50%', background: '#22C55E' }} />
            <span style={{ fontSize: 11, color: muted }}>RUPP Lost & Found</span>
          </div>
        </div>
        <button
          onClick={toggleDark}
          style={{
            width: 38,
            height: 38,
            borderRadius: '50%',
            background: isDark ? 'rgba(126,170,255,0.12)' : 'rgba(21,101,192,0.08)',
            border: `1px solid ${isDark ? 'rgba(126,170,255,0.2)' : 'rgba(21,101,192,0.12)'}`,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            transition: 'background 0.2s',
          }}
        >
          {isDark ? <Sun size={17} color={primary} /> : <Moon size={17} color={primary} />}
        </button>
      </div>

      {/* Search + Filters sticky area */}
      <div
        style={{
          background: isDark ? 'rgba(26,28,37,0.9)' : 'rgba(255,255,255,0.9)',
          backdropFilter: 'blur(16px)',
          WebkitBackdropFilter: 'blur(16px)',
          borderBottom: `1px solid ${border}`,
          flexShrink: 0,
        }}
      >
        {/* Stat chips row */}
        <div style={{ display: 'flex', gap: 6, padding: '8px 16px 6px' }}>
          {[
            { label: `${lostCount} Lost`, bg: isDark ? '#3B1212' : '#FEE2E2', text: isDark ? '#FCA5A5' : '#C62828' },
            { label: `${foundCount} Found`, bg: isDark ? '#052E16' : '#DCFCE7', text: isDark ? '#86EFAC' : '#15803D' },
            { label: `${mockItems.length} Total`, bg: isDark ? '#0D2B6B' : '#DBEAFE', text: isDark ? '#93BBFD' : '#1D4ED8' },
          ].map(({ label, bg: chipBg, text }) => (
            <div
              key={label}
              style={{
                padding: '3px 10px',
                borderRadius: 20,
                background: chipBg,
                color: text,
                fontSize: 11,
                fontWeight: 600,
                fontFamily: 'Roboto, system-ui, sans-serif',
              }}
            >
              {label}
            </div>
          ))}
        </div>

        {/* Search bar */}
        <div style={{ padding: '0 16px 8px' }}>
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: 8,
              background: searchBg,
              borderRadius: 10,
              padding: '9px 13px',
              border: `1.5px solid ${border}`,
              boxShadow: isDark ? 'none' : '0 1px 4px rgba(0,0,0,0.04)',
            }}
          >
            <Search size={15} color={muted} />
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search lost & found items..."
              style={{
                flex: 1,
                background: 'none',
                border: 'none',
                outline: 'none',
                fontSize: 13,
                color: onSurface,
                fontFamily: 'Roboto, system-ui, sans-serif',
              }}
            />
            {search && (
              <button
                onClick={() => setSearch('')}
                style={{ background: 'none', border: 'none', cursor: 'pointer', color: muted, fontSize: 16, lineHeight: 1, padding: 0 }}
              >
                ×
              </button>
            )}
          </div>
        </div>

        {/* Filter chips */}
        <div
          style={{
            display: 'flex',
            gap: 6,
            padding: '0 16px 10px',
            overflowX: 'auto',
            scrollbarWidth: 'none',
            msOverflowStyle: 'none',
          }}
        >
          {FILTERS.map(f => {
            const isActive = activeFilter === f;
            return (
              <button
                key={f}
                onClick={() => setActiveFilter(f)}
                style={{
                  flexShrink: 0,
                  padding: '5px 13px',
                  borderRadius: 20,
                  border: `1.5px solid ${isActive ? 'transparent' : chipInactiveBorder}`,
                  background: isActive ? chipActiveBg : 'transparent',
                  color: isActive ? '#fff' : (isDark ? '#9EA3AE' : '#44474E'),
                  fontSize: 12,
                  fontWeight: isActive ? 600 : 400,
                  cursor: 'pointer',
                  fontFamily: 'Roboto, system-ui, sans-serif',
                  whiteSpace: 'nowrap',
                  transition: 'all 0.15s',
                  boxShadow: isActive ? (isDark ? '0 2px 8px rgba(126,170,255,0.2)' : '0 2px 8px rgba(21,101,192,0.2)') : 'none',
                }}
              >
                {f}
              </button>
            );
          })}
        </div>
      </div>

      {/* Feed — with ambient radial glow at top */}
      <div
        style={{
          flex: 1,
          overflowY: 'auto',
          padding: '12px 16px 8px',
          display: 'flex',
          flexDirection: 'column',
          gap: 8,
          backgroundImage: isDark
            ? 'radial-gradient(ellipse 80% 30% at 50% 0%, rgba(21,101,192,0.08) 0%, transparent 100%)'
            : 'radial-gradient(ellipse 80% 30% at 50% 0%, rgba(21,101,192,0.05) 0%, transparent 100%)',
        }}
      >
        {/* Result info row */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <Sparkles size={12} color={muted} />
            <span style={{ fontSize: 12, color: muted, fontWeight: 500 }}>
              {filtered.length} result{filtered.length !== 1 ? 's' : ''}
              {activeFilter !== 'All' && ` for "${activeFilter}"`}
            </span>
          </div>
        </div>

        {filtered.map(item => (
          <ItemCard
            key={item.id}
            item={item}
            isDark={isDark}
            onClick={() => navigate('detail', item.id)}
          />
        ))}

        {filtered.length === 0 && (
          <div
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              padding: '40px 20px',
              gap: 10,
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
              <Search size={24} color={isDark ? '#5A5F70' : '#93A8D0'} strokeWidth={1.5} />
            </div>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: 14, fontWeight: 600, color: onSurface }}>No items found</div>
              <div style={{ fontSize: 12, color: muted, marginTop: 4 }}>
                Try a different search term or filter
              </div>
            </div>
          </div>
        )}

        <div style={{ height: 80 }} />
      </div>

      {/* FAB */}
      <button
        onClick={() => navigate('report')}
        style={{
          position: 'absolute',
          bottom: 96,
          right: 18,
          width: 56,
          height: 56,
          borderRadius: 16,
          background: isDark
            ? 'linear-gradient(135deg, #4D8FFF 0%, #7EAAFF 100%)'
            : 'linear-gradient(135deg, #1565C0 0%, #1E88E5 100%)',
          border: 'none',
          cursor: 'pointer',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: isDark
            ? '0 4px 20px rgba(126,170,255,0.32), 0 0 0 1px rgba(126,170,255,0.15)'
            : '0 4px 20px rgba(21,101,192,0.38), 0 0 0 1px rgba(21,101,192,0.1)',
          zIndex: 10,
          transition: 'transform 0.15s, box-shadow 0.15s',
        }}
        onMouseEnter={e => {
          (e.currentTarget as HTMLElement).style.transform = 'scale(1.06)';
        }}
        onMouseLeave={e => {
          (e.currentTarget as HTMLElement).style.transform = 'scale(1)';
        }}
      >
        <Plus size={24} color="#fff" strokeWidth={2.5} />
      </button>

      <BottomNav active="home" isDark={isDark} navigate={navigate} />
    </div>
  );
}
