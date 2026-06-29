interface Props {
  status: 'Lost' | 'Found' | 'Claimed';
  isDark?: boolean;
}

const CONFIGS = {
  Lost: {
    light: { bg: '#FEE2E2', text: '#C62828', dot: '#EF4444' },
    dark: { bg: '#3B1212', text: '#FCA5A5', dot: '#EF4444' },
  },
  Found: {
    light: { bg: '#DCFCE7', text: '#15803D', dot: '#22C55E' },
    dark: { bg: '#052E16', text: '#86EFAC', dot: '#22C55E' },
  },
  Claimed: {
    light: { bg: '#F3E8FF', text: '#7E22CE', dot: '#A855F7' },
    dark: { bg: '#2E1065', text: '#C4B5FD', dot: '#A855F7' },
  },
};

export function StatusBadge({ status, isDark = false }: Props) {
  const cfg = CONFIGS[status];
  const c = isDark ? cfg.dark : cfg.light;

  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: 5,
        background: c.bg,
        color: c.text,
        borderRadius: 6,
        padding: '3px 8px',
        fontSize: 11,
        fontWeight: 600,
        fontFamily: 'Roboto, system-ui, sans-serif',
        letterSpacing: '0.04em',
        flexShrink: 0,
        whiteSpace: 'nowrap',
      }}
    >
      <span
        style={{ width: 6, height: 6, borderRadius: '50%', background: c.dot, flexShrink: 0 }}
      />
      {status}
    </span>
  );
}
