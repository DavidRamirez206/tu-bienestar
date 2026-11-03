import useAuth from "./useAuth";

export default function useAuthorities() {
  const { user } = useAuth();
  return (authority: string) => user?.authorities?.includes(authority) ?? false;
}
