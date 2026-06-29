import { useState } from 'react';
import { Wifi, Signal, Battery } from 'lucide-react';
import { LoginScreen } from './components/screens/LoginScreen';
import { HomeScreen } from './components/screens/HomeScreen';
import { ReportScreen } from './components/screens/ReportScreen';
import { DetailScreen } from './components/screens/DetailScreen';
import { ProfileScreen } from './components/screens/ProfileScreen';

export type NavScreen = 'login' | 'home' | 'report' | 'detail' | 'profile';

export interface AppProps {
  isDark: boolean;
  navigate: (screen: NavScreen, itemId?: number) => void;
  toggleDark: () => void;
  selectedItemId: number | null;
}

const SCREEN_LABELS: Record<NavScreen, string> = {
  login: 'Login',
  home: 'Home',
  report: 'Report',
  detail: 'Detail',
  profile: 'Profile',
};

export default function App() {
  const [screen, setScreen] = useState<NavScreen>('login');
  const [isDark, setIsDark] = useState(false);
  const [selectedItemId, setSelectedItemId] = useState<number | null>(1);

  const navigate = (newScreen: NavScreen, itemId?: number) => {
    setScreen(newScreen);
    if (itemId != null) setSelectedItemId(itemId);
  };

  const toggleDark = () => setIsDark(d => !d);

  const props: AppProps = { isDark, navigate, toggleDark, selectedItemId };

  const pageBg = isDark
    ? 'radial-gradient(ellipse 120% 80% at 50% -10%, #1a2040 0%, #0a0c14 70%)'
    : 'radial-gradient(ellipse 120% 80% at 50% -10%, #C7D9F5 0%, #EEF3FC 60%, #E8EDF8 100%)';

  const transitionStyle = 'background 0.3s ease, color 0.25s ease';

  const frameOuterRing = isDark ? '#1C1F2A' : '#CDD5E8';
  const frameInnerRing = isDark ? '#14161E' : '#EEF2FA';
  const statusBg = isDark ? '#0E1016' : '#F0F4FA';
  const statusText = isDark ? '#E2E3E8' : '#1C1B1F';
  const labelColor = isDark ? '#60708A' : '#8099C0';

  return (
    <div
      style={{
        minHeight: '100vh',
        background: pageBg,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '24px 16px',
        gap: 20,
        fontFamily: 'Roboto, system-ui, sans-serif',
        transition: transitionStyle,
      }}
    >
      {/* Page header */}
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontSize: 13, fontWeight: 600, color: labelColor, letterSpacing: '0.12em', textTransform: 'uppercase' }}>
          Campus Found — MD3 Redesign
        </div>
        <div style={{ fontSize: 12, color: labelColor, opacity: 0.7, marginTop: 3 }}>
          Interactive prototype · {SCREEN_LABELS[screen]} screen
        </div>
      </div>

      {/* Phone frame */}
      <div
        style={{
          position: 'relative',
          width: 393,
          flexShrink: 0,
        }}
      >
        {/* Outer bezel */}
        <div
          style={{
            width: 393,
            height: 852,
            background: frameOuterRing,
            borderRadius: 52,
            padding: 8,
            transition: 'background 0.3s ease, box-shadow 0.3s ease',
            boxShadow: isDark
              ? '0 40px 100px rgba(0,0,0,0.7), 0 10px 30px rgba(0,0,0,0.5)'
              : '0 40px 100px rgba(30,50,100,0.22), 0 10px 30px rgba(30,50,100,0.1)',
          }}
        >
          {/* Inner bezel */}
          <div
            style={{
              width: '100%',
              height: '100%',
              background: frameInnerRing,
              borderRadius: 46,
              padding: 3,
              overflow: 'hidden',
            }}
          >
            {/* Screen */}
            <div
              style={{
                width: '100%',
                height: '100%',
                background: statusBg,
                borderRadius: 44,
                overflow: 'hidden',
                display: 'flex',
                flexDirection: 'column',
                position: 'relative',
              }}
            >
              {/* Dynamic island */}
              <div
                style={{
                  position: 'absolute',
                  top: 12,
                  left: '50%',
                  transform: 'translateX(-50%)',
                  width: 120,
                  height: 34,
                  background: '#000',
                  borderRadius: 18,
                  zIndex: 200,
                  pointerEvents: 'none',
                }}
              />

              {/* Status bar */}
              <div
                style={{
                  height: 54,
                  background: statusBg,
                  display: 'flex',
                  alignItems: 'flex-end',
                  justifyContent: 'space-between',
                  padding: '0 28px 10px',
                  flexShrink: 0,
                  zIndex: 10,
                  position: 'relative',
                }}
              >
                <span style={{ fontSize: 14, fontWeight: 700, color: statusText, letterSpacing: '-0.2px' }}>
                  9:41
                </span>
                <div style={{ display: 'flex', gap: 5, alignItems: 'center' }}>
                  <Signal size={14} color={statusText} strokeWidth={2} />
                  <Wifi size={14} color={statusText} strokeWidth={2} />
                  <Battery size={14} color={statusText} strokeWidth={2} />
                </div>
              </div>

              {/* Screen content area */}
              <div style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
                {screen === 'login' && (
                  <LoginScreen {...props} onLogin={() => navigate('home')} />
                )}
                {screen === 'home' && <HomeScreen {...props} />}
                {screen === 'report' && <ReportScreen {...props} />}
                {screen === 'detail' && <DetailScreen {...props} />}
                {screen === 'profile' && <ProfileScreen {...props} />}
              </div>
            </div>
          </div>
        </div>

        {/* Side buttons (decorative) */}
        <div style={{ position: 'absolute', right: -3, top: 160, width: 4, height: 70, background: frameOuterRing, borderRadius: '0 3px 3px 0' }} />
        <div style={{ position: 'absolute', left: -3, top: 140, width: 4, height: 44, background: frameOuterRing, borderRadius: '3px 0 0 3px' }} />
        <div style={{ position: 'absolute', left: -3, top: 200, width: 4, height: 44, background: frameOuterRing, borderRadius: '3px 0 0 3px' }} />
        <div style={{ position: 'absolute', left: -3, top: 258, width: 4, height: 60, background: frameOuterRing, borderRadius: '3px 0 0 3px' }} />
      </div>

      {/* Screen navigator */}
      <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', justifyContent: 'center' }}>
        {(Object.keys(SCREEN_LABELS) as NavScreen[]).map(s => {
          const isActive = screen === s;
          return (
            <button
              key={s}
              onClick={() => navigate(s)}
              onMouseEnter={e => {
                if (!isActive) (e.currentTarget as HTMLElement).style.transform = 'translateY(-2px)';
              }}
              onMouseLeave={e => {
                (e.currentTarget as HTMLElement).style.transform = 'translateY(0)';
              }}
              style={{
                padding: '7px 16px',
                borderRadius: 20,
                border: `1.5px solid ${isActive ? (isDark ? '#7EAAFF' : '#1565C0') : (isDark ? 'rgba(255,255,255,0.12)' : 'rgba(21,101,192,0.2)')}`,
                background: isActive
                  ? (isDark ? '#7EAAFF' : '#1565C0')
                  : (isDark ? 'rgba(255,255,255,0.04)' : 'rgba(255,255,255,0.72)'),
                color: isActive ? '#fff' : labelColor,
                fontSize: 12,
                fontWeight: isActive ? 600 : 400,
                cursor: 'pointer',
                fontFamily: 'Roboto, system-ui, sans-serif',
                letterSpacing: '0.03em',
                transition: 'all 0.18s',
                backdropFilter: 'blur(10px)',
                boxShadow: isActive
                  ? (isDark ? '0 4px 14px rgba(126,170,255,0.3)' : '0 4px 14px rgba(21,101,192,0.28)')
                  : '0 1px 4px rgba(0,0,0,0.06)',
              }}
            >
              {SCREEN_LABELS[s]}
            </button>
          );
        })}
      </div>

      <div style={{ fontSize: 11, color: labelColor, opacity: 0.6, textAlign: 'center', maxWidth: 360 }}>
        Navigate using the bottom nav inside the phone, or use the screen buttons above.
        Toggle dark mode with the moon/sun icon in the app bar.
      </div>
    </div>
  );
}
