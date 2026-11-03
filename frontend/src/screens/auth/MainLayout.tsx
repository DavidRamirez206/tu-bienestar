import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import Paper from "@mui/material/Paper";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import { logout as logoutRequest } from "../../services/AuthService";
import GlobalSnackbar from "../../components/common/GlobalSnackbar";
import MenuIcon from "@mui/icons-material/Menu";
import { useTheme } from "@mui/material/styles";
import useMediaQuery from "@mui/material/useMediaQuery";
import CssBaseline from "@mui/material/CssBaseline";

import HomeIcon from "@mui/icons-material/Home";
import FitnessCenterIcon from "@mui/icons-material/FitnessCenter";
import DirectionsRunIcon from "@mui/icons-material/DirectionsRun";
import EventIcon from "@mui/icons-material/Event";
import ShowChartIcon from "@mui/icons-material/ShowChart";
import PersonIcon from "@mui/icons-material/Person";
import LogoutIcon from "@mui/icons-material/Logout";
import ListItemIcon from "@mui/material/ListItemIcon";
import React from "react";

const DRAWER_WIDTH_FULL = 260;
const DRAWER_WIDTH_COLLAPSED = 88; // Width when collapsed to show only icons

type NavItem = {
  label: string;
  path: string;
  icon: React.ReactNode;
  requiredAuthorities?: string[];
  divider?: boolean;
};

// Cada historia agrega aqui su entrada del menu.
const navItems: NavItem[] = [
  {
    label: "Inicio",
    path: "/",
    icon: <HomeIcon />,
  },
  {
    label: "Rutinas",
    path: "/routines",
    icon: <FitnessCenterIcon />,
  },
  {
    label: "Ejercicios",
    path: "/exercises",
    icon: <DirectionsRunIcon />,
    requiredAuthorities: ["LIST_EXERCISE"],
  },
  {
    label: "Actividades",
    path: "/activities",
    icon: <EventIcon />,
    requiredAuthorities: ["LIST_ACTIVITY"],
  },
  {
    label: "Progreso",
    path: "/progress",
    icon: <ShowChartIcon />,
  },
  {
    label: "Mi perfil",
    path: "/profile",
    icon: <PersonIcon />,
  },
];

const MainLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("lg"));

  const [mobileOpen, setMobileOpen] = React.useState(false);
  const [isCollapsed, setIsCollapsed] = React.useState(false);

  const handleDrawerToggle = () => {
    if (isMobile) {
      setMobileOpen(!mobileOpen);
    } else {
      setIsCollapsed(!isCollapsed);
    }
  };

  const handleLogout = async () => {
    await logoutRequest();
    logout();
    navigate("/login");
  };

  const hasAuthority = (requiredAuthorities?: string[]) => {
    if (!requiredAuthorities || requiredAuthorities.length === 0) {
      return true;
    }

    return requiredAuthorities.some((authority) => {
      if (user?.authorities?.includes(authority)) {return true;}
      
      const cleanAuthority = authority.startsWith('ROLE_') ? authority.substring(5) : authority;
      if (user?.authorities?.includes(cleanAuthority)) {return true;}

      if (authority.startsWith('EDIT_') || authority.startsWith('LIST_') || authority.startsWith('DELETE_')) {
          if (user?.authorities?.includes('ROLE_ADMIN') || user?.authorities?.includes('ADMIN')) {return true;}
      }

      return false;
    });
  };

  const drawerWidth = isMobile ? DRAWER_WIDTH_FULL : (isCollapsed ? DRAWER_WIDTH_COLLAPSED : DRAWER_WIDTH_FULL);

  const drawerContent = (
    <>
      <Box sx={{ p: 3, display: 'flex', alignItems: 'center', justifyContent: isCollapsed ? 'center' : 'flex-start' }}>
        <Typography
          variant="h5"
          noWrap
          sx={{ 
            fontWeight: 800, 
            color: "primary.main", 
            letterSpacing: "-0.5px",
            display: isCollapsed && !isMobile ? 'none' : 'block'
          }}
        >
          Mi Bienestar
        </Typography>
      </Box>

      <Divider />

      <List sx={{ px: 1.5, py: 2, flexGrow: 1 }}>
        {navItems
          .filter((item) => hasAuthority(item.requiredAuthorities))
          .map((item) => {
            const selected = location.pathname === item.path;
            return (
              <React.Fragment key={item.path}>
                  <ListItemButton
                  onClick={() => {
                    navigate(item.path);
                    setMobileOpen(false);
                  }}
                  selected={selected}
                  sx={{
                      borderRadius: 2,
                      mb: 0.5,
                      py: 1,
                      justifyContent: isCollapsed ? 'center' : 'initial',
                      "&.Mui-selected": {
                      backgroundColor: "primary.main",
                      color: "white",
                      "&:hover": {
                          backgroundColor: "primary.dark",
                      },
                      "& .MuiListItemIcon-root": {
                          color: "white",
                      },
                      "& .MuiListItemText-primary": {
                          fontWeight: 700,
                      },
                      },
                      "&:hover": {
                      backgroundColor: "rgba(83, 83, 238, 0.08)",
                      "& .MuiListItemIcon-root": {
                          color: "primary.main",
                      },
                      },
                  }}
                  >
                  <ListItemIcon 
                      sx={{ 
                          minWidth: 0,
                          justifyContent: 'center',
                          mr: isCollapsed ? 0 : 3,
                          color: selected ? "white" : "text.secondary",
                          transition: 'color 0.2s' 
                      }}
                  >
                      {item.icon}
                  </ListItemIcon>
                  <ListItemText
                      primary={item.label}
                      sx={{
                      opacity: isCollapsed && !isMobile ? 0 : 1,
                      "& .MuiListItemText-primary": {
                          fontSize: "0.9rem",
                          fontWeight: selected ? 700 : 600,
                      },
                      }}
                  />
                  </ListItemButton>
                  {item.divider && <Divider sx={{ my: 2, mx: 1 }} />}
              </React.Fragment>
            );
          })}
      </List>

      <Divider />

      <Box sx={{ p: 2 }}>
        {isCollapsed && !isMobile ? (
          <IconButton
            aria-label="Cerrar sesión"
            title="Cerrar sesión"
            color="error"
            onClick={handleLogout}
            sx={{ display: "flex", mx: "auto" }}
          >
            <LogoutIcon />
          </IconButton>
        ) : (
        <Button
          fullWidth
          variant="outlined"
          color="error"
          onClick={handleLogout}
          sx={{ 
            borderRadius: 2, 
            py: 1.2, 
            textTransform: "none", 
            fontWeight: 600,
            borderWidth: 2,
            "&:hover": {
              borderWidth: 2,
            },
          }}
        >
          Cerrar sesión
        </Button>
        )}
      </Box>
    </>
  );

  return (
    <Box
      sx={{ display: "flex", minHeight: "100vh", backgroundColor: "#f5f7fb" }}
    >
      <CssBaseline />
      <Drawer
        variant={isMobile ? "temporary" : "permanent"}
        open={isMobile ? mobileOpen : true}
        onClose={isMobile ? handleDrawerToggle : undefined}
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
            display: "flex",
            flexDirection: "column",
            borderRight: "1px solid",
            borderColor: "divider",
            backgroundColor: "white",
            transition: theme.transitions.create('width', {
              easing: theme.transitions.easing.sharp,
              duration: theme.transitions.duration.enteringScreen,
            }),
            overflowX: 'hidden',
          },
        }}
      >
        {drawerContent}
      </Drawer>

      <Box component="main" sx={{ 
        flexGrow: 1, 
        minWidth: 0,
        p: { xs: 2, md: 4 }, 
        overflowX: "hidden",
      }}>
        <Paper
          elevation={0}
          sx={{
            position: "sticky",
            top: { xs: 8, md: 24 },
            zIndex: 1100, // Higher than drawer
            borderRadius: 4,
            p: { xs: 2, md: 3 },
            mb: 4,
            border: "1px solid",
            borderColor: "divider",
            backgroundColor: "white",
            boxShadow: "0 4px 20px 0 rgba(0,0,0,0.05)",
          }}
        >
          <Stack
            direction="row"
            spacing={2}
            sx={{
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <Stack direction="row" sx={{ alignItems: 'center', minWidth: 0 }} spacing={1}>
              <IconButton
                color="inherit"
                aria-label="open drawer"
                onClick={handleDrawerToggle}
                edge="start"
                sx={{ mr: { xs: 0, md: 2 } }}
              >
                <MenuIcon />
              </IconButton>
              <Box sx={{ minWidth: 0 }}>
                <Typography
                  variant="h4"
                  color="text.primary"
                  sx={{ fontWeight: 800, fontSize: { xs: "1.5rem", md: "2rem" } }}
                >
                  Bienvenido{user?.firstName ? `, ${user?.firstName}` : ""}
                </Typography>

                {user?.institutionalEmail && (
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mt: 0.5, fontWeight: 500, overflowWrap: "anywhere" }}
                  >
                    Sesión iniciada: {user?.institutionalEmail}
                  </Typography>
                )}
              </Box>
            </Stack>

          </Stack>
        </Paper>

        <Outlet />
      </Box>

      {/* Global components */}
      <GlobalSnackbar />
    </Box>
  );
};

export default MainLayout;