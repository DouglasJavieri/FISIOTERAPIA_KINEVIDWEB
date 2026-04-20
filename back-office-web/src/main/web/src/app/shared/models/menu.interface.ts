export interface MenuItem {
  id: string | null;
  label: string | null;
  icon: string | null;
  route: string | null;
  children?: MenuItem[];
  active?: boolean | null;
}
