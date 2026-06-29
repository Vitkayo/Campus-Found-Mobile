import { Home, FilePlus2, User } from 'lucide-react';
import type { NavScreen } from '../App';

interface Props {
  active: 'home' | 'report' | 'profile';
  isDark: boolean;
  navigate: (screen: NavScreen) => void;
}

const TABS = [
  { id: 'home' as const, label: 'Home', Icon: Home, screen: 'home' as NavScreen },
  { id: 'report' as const, label: 'Report', Icon: FilePlus2, screen: 'report' as NavScreen },
  { id: 'profile' as const, label: 'Profile', Icon: User, screen: 'profile' as NavScreen },
];

export function BottomNav({ active, isDark, navigate }: Props) {
  const bg = isDark ? '#1A1C25' : '#FFFFFF';
  const borderColor = isDark ? '#2A2D35' : '#F0F2F8';
  const primary = isDark ? '#7EAAFF' : '#1565C0';
  const inactive = isDark ? '#5A5F70' : '#9CA3AF';

  return (
    <div
      style={{
        height: 80,
        background: bg,
        borderTop: `1px solid ${borderColor}`,
        display: 'flex',
        alignItems: 'stretch',
        flexShrink: 0,
        paddingBottom: 4,
      }}
    >
      {TABS.map(({ id, label, Icon, screen }) => {
        const isActive = active === id;
        return (
          <button
            key={id}
            onClick={() => navigate(screen)}
            style={{
              flex: 1,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 3,
              border: 'none',
              background: 'transparent',
              cursor: 'pointer',
              padding: '8px 0 4px',
              position: 'relative',
            }}
          >
            {isActive && (
              <div
                style={{
                  position: 'absolute',
                  top: '50%',
                  left: '50%',
                  transform: 'translate(-50%, -60%)',
                  width: 64,
                  height: 32,
                  background: isDark
                    ? 'rgba(126, 170, 255, 0.14)'
                    : 'rgba(21, 101, 192, 0.1)',
                  borderRadius: 16,
                  pointerEvents: 'none',
                }}
              />
            )}
            <Icon
              size={22}
              color={isActive ? primary : inactive}
              strokeWidth={isActive ? 2.2 : 1.6}
              style={{ position: 'relative', zIndex: 1 }}
            />
            <span
              style={{
                fontSize: 11,
                fontWeight: isActive ? 600 : 400,
                color: isActive ? primary : inactive,
                fontFamily: 'Roboto, system-ui, sans-serif',
                letterSpacing: '0.02em',
                position: 'relative',
                zIndex: 1,
              }}
            >
              {label}
            </span>
          </button>
        );
      })}
    </div>
  );
}
