import type { ReactNode } from 'react';
import useRealTime from '../../hooks/useRealTime';

/** Mantiene la conexion en tiempo real mientras sus hijos esten montados. */
const RealTimeWrapper = ({ children }: { children: ReactNode }) => {
  useRealTime();
  return <>{children}</>;
};

export default RealTimeWrapper;
