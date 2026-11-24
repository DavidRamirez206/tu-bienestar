import React, { useEffect, useState, useMemo } from 'react';
import {
    Box,
    Typography,
    Paper,
    CircularProgress,
    Alert,
    Card,
    CardContent,
    List,
    ListItem,
    ListItemText,
    ListItemAvatar,
    Divider,
    Chip,
    Tabs,
    Tab,
    Stack,
    Avatar,
    Button
} from '@mui/material';
import Grid from '@mui/material/Grid';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter';
import ShowChartIcon from '@mui/icons-material/ShowChart';
import WorkspacePremiumIcon from '@mui/icons-material/WorkspacePremium';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import {getEnrollmentsByUserId} from '../../services/EnrollmentService';
import {getAllProgress} from '../../services/ProgressService';
import {getAllExercises} from '../../services/ExerciseService';
import {getAllRoutines} from '../../services/RoutineService';
import type {EnrollmentResponse, ProgressResponse, ExerciseResponse, RoutineResponse} from '../../types/api.types';
import useAuth from '../../hooks/useAuth';
import SimpleBarChart from '../../components/common/SimpleBarChart';
import { calculateDashboardStats } from '../../utils/stats.utils';

const HistoryScreen: React.FC = () => {
    const [enrollments, setEnrollments] = useState<EnrollmentResponse[]>([]);
    const [progress, setProgress] = useState<ProgressResponse[]>([]);
    const [exercises, setExercises] = useState<ExerciseResponse[]>([]);
    const [, setRoutines] = useState<RoutineResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [tabValue, setTabValue] = useState(0);
    const {user} = useAuth();

    useEffect(() => {
        if (!user?.idUser) {return;}

        const loadData = async () => {
            try {
                setLoading(true);
                const [enrollRes, progressRes, exercisesRes, routinesRes] = await Promise.all([
                    getEnrollmentsByUserId(user.idUser),
                    getAllProgress(),
                    getAllExercises(),
                    getAllRoutines()
                ]);

                if (enrollRes.data) {setEnrollments(enrollRes.data);}
                if (progressRes.data) {setProgress(progressRes.data);}
                if (exercisesRes.data) {setExercises(exercisesRes.data);}
                if (routinesRes.data) {setRoutines(routinesRes.data);}
            } catch {
                setError('Error al cargar el historial.');
            } finally {
                setLoading(false);
            }
        };

        void loadData();
    }, [user?.idUser]);

    const handleExportPDF = async () => {
        const reportEl = document.getElementById('pdf-report-content');
        if (!reportEl) {return;}

        const canvas = await html2canvas(reportEl, {
            scale: 2,
            useCORS: true,
            backgroundColor: '#ffffff',
        });

        const imgData = canvas.toDataURL('image/png');
        const pdf = new jsPDF('p', 'mm', 'a4');
        const pdfWidth = pdf.internal.pageSize.getWidth();
        const pdfHeight = (canvas.height * pdfWidth) / canvas.width;

        pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
        pdf.save(`reporte-${user?.firstName?.toLowerCase()}-${new Date().toISOString().split('T')[0]}.pdf`);
    };

    const exerciseMap = useMemo(() => new Map(exercises.map(ex => [ex.idExercise, ex])), [exercises]);

    const pastActivities = useMemo(() => {
        const now = new Date();
        return enrollments.filter(e => e.activityEndDate && new Date(e.activityEndDate) < now);
    }, [enrollments]);

    const sortedProgress = useMemo(() => {
        return [...progress].sort((a, b) => new Date(b.dateLogged).getTime() - new Date(a.dateLogged).getTime());
    }, [progress]);

    const stats = useMemo(() => calculateDashboardStats(progress), [progress]);

    if (loading) {
        return (
            <Box sx={{display: 'flex', justifyContent: 'center', py: 8}}>
                <CircularProgress thickness={5}/>
            </Box>
        );
    }

    return (
        <Box>
            {/* Header con botón */}
            <Box sx={{ mb: 5, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Box>
                    <Typography variant="h4" sx={{ fontWeight: 900, letterSpacing: '-1.5px', color: 'primary.main' }}>
                        Panel de Rendimiento
                    </Typography>
                    <Typography variant="body1" color="text.secondary" sx={{ mt: 0.5, fontWeight: 500 }}>
                        Seguimiento detallado de tu evolución física y actividades.
                    </Typography>
                </Box>
                <Button
                    variant="contained"
                    startIcon={<FileDownloadIcon />}
                    onClick={handleExportPDF}
                    sx={{ borderRadius: 2, fontWeight: 700, px: 3, py: 1.2 }}
                >
                    Descargar Reporte PDF
                </Button>
            </Box>

            {error && <Alert severity="error" sx={{ mb: 3, borderRadius: 3 }}>{error}</Alert>}

            {/* KPI Cards (visibles en pantalla) */}
            <Grid container spacing={3} sx={{ mb: 5 }}>
                <Grid size={{ xs: 6, sm: 6, md: 3 }}>
                    <Card elevation={0} sx={{ bgcolor: 'primary.main', color: 'white', borderRadius: 5, height: '100%' }}>
                        <CardContent sx={{ p: 3 }}>
                            <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
                                <Box>
                                    <Typography variant="h3" sx={{ fontWeight: 900 }}>{stats.totalSessions}</Typography>
                                    <Typography variant="caption" sx={{ fontWeight: 600, opacity: 0.9 }}>Sesiones Totales</Typography>
                                </Box>
                                <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}><ShowChartIcon /></Avatar>
                            </Stack>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid size={{ xs: 6, sm: 6, md: 3 }}>
                    <Card elevation={0} sx={{ bgcolor: 'secondary.main', color: 'white', borderRadius: 5, height: '100%' }}>
                        <CardContent sx={{ p: 3 }}>
                            <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
                                <Box>
                                    <Typography variant="h3" sx={{ fontWeight: 900 }}>{stats.maxWeight}kg</Typography>
                                    <Typography variant="caption" sx={{ fontWeight: 600, opacity: 0.9 }}>Peso Máximo</Typography>
                                </Box>
                                <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}><WorkspacePremiumIcon /></Avatar>
                            </Stack>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid size={{ xs: 6, sm: 6, md: 3 }}>
                    <Card elevation={0} sx={{ bgcolor: 'info.main', color: 'white', borderRadius: 5, height: '100%' }}>
                        <CardContent sx={{ p: 3 }}>
                            <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
                                <Box>
                                    <Typography variant="h3" sx={{ fontWeight: 900 }}>{stats.activeDaysThisWeek}</Typography>
                                    <Typography variant="caption" sx={{ fontWeight: 600, opacity: 0.9 }}>Días Activos (Semana)</Typography>
                                </Box>
                                <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}><LocalFireDepartmentIcon /></Avatar>
                            </Stack>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid size={{ xs: 6, sm: 6, md: 3 }}>
                    <Card elevation={0} sx={{ bgcolor: 'success.main', color: 'white', borderRadius: 5, height: '100%' }}>
                        <CardContent sx={{ p: 3 }}>
                            <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'flex-start' }}>
                                <Box>
                                    <Typography variant="h3" sx={{ fontWeight: 900 }}>{stats.avgReps}</Typography>
                                    <Typography variant="caption" sx={{ fontWeight: 600, opacity: 0.9 }}>Reps Promedio</Typography>
                                </Box>
                                <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.2)' }}><FitnessCenterIcon /></Avatar>
                            </Stack>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Tabs interactivas (visibles en pantalla) */}
            <Paper sx={{ borderRadius: 6, overflow: 'hidden', mb: 4, boxShadow: '0 10px 40px 0 rgba(0,0,0,0.05)', border: '1px solid', borderColor: 'divider' }}>
                <Tabs
                    value={tabValue}
                    onChange={(_, v) => setTabValue(v)}
                    variant="scrollable"
                    scrollButtons="auto"
                    sx={{ px: 2, pt: 1, borderBottom: '1px solid', borderColor: 'divider' }}
                >
                    <Tab label="Estadísticas" sx={{ fontWeight: 800, px: 4 }} />
                    <Tab label="Historial de Logs" sx={{ fontWeight: 800, px: 4 }} />
                    <Tab label="Actividades" sx={{ fontWeight: 800, px: 4 }} />
                </Tabs>

                <Box sx={{ p: 4 }}>
                    {tabValue === 0 && (
                        <Grid container spacing={5}>
                            <Grid size={{ xs: 12, lg: 6 }}>
                                <SimpleBarChart data={stats.weeklyReps} title="Repeticiones por Día" subtitle="Últimos 7 días" unit="reps" />
                            </Grid>
                            <Grid size={{ xs: 12, lg: 6 }}>
                                <SimpleBarChart data={stats.monthlyVolume} title="Carga Mensual" subtitle="Volumen acumulado" color="secondary.main" unit="kg" />
                            </Grid>
                            <Grid size={{ xs: 12 }}>
                                <Box sx={{ p: 4, textAlign: 'center', bgcolor: 'primary.main', color: 'white', borderRadius: 5 }}>
                                    <Typography variant="h5" sx={{ fontWeight: 900, mb: 1 }}>
                                        {stats.totalSessions > 0 ? '¡Excelente trabajo!' : '¡Empecemos hoy!'}
                                    </Typography>
                                    <Typography variant="body1" sx={{ opacity: 0.9 }}>
                                        Has completado <strong>{stats.totalSessions}</strong> registros en total.
                                    </Typography>
                                </Box>
                            </Grid>
                        </Grid>
                    )}

                    {tabValue === 1 && (
                        <List sx={{ py: 0 }}>
                            {sortedProgress.length === 0 ? (
                                <Box sx={{ py: 10, textAlign: 'center' }}>
                                    <Typography color="text.secondary" sx={{ fontWeight: 600 }}>No hay registros de progreso.</Typography>
                                </Box>
                            ) : (
                                sortedProgress.slice(0, 10).map((p, i) => (
                                    <React.Fragment key={`${p.idProgress}-${i}`}>
                                        <ListItem sx={{ py: 2.5, px: 1 }}>
                                            <ListItemAvatar>
                                                <Avatar sx={{ bgcolor: 'rgba(83, 83, 238, 0.1)', color: 'primary.main' }}>
                                                    <FitnessCenterIcon fontSize="small" />
                                                </Avatar>
                                            </ListItemAvatar>
                                            <ListItemText
                                                primary={<Typography variant="subtitle1" sx={{ fontWeight: 800 }}>{exerciseMap.get(p.exerciseId)?.name || 'Ejercicio'}</Typography>}
                                                secondary={
                                                    <Stack direction="row" spacing={2} component="span" sx={{ mt: 0.5, display: 'flex' }}>
                                                        <Typography variant="caption" component="span" sx={{ fontWeight: 600 }}>{new Date(p.dateLogged).toLocaleString()}</Typography>
                                                        <Typography variant="caption" component="span" color="primary" sx={{ fontWeight: 800 }}>{p.reps} Reps</Typography>
                                                        <Typography variant="caption" component="span" color="secondary" sx={{ fontWeight: 800 }}>{p.weightKg} kg</Typography>
                                                    </Stack>
                                                }
                                                slotProps={{ secondary: { component: 'span' } }}
                                            />
                                            <Chip label={`${p.effortLevel}/10`} size="small" sx={{ fontWeight: 800, borderRadius: 1.5, bgcolor: 'success.light', color: 'success.dark' }} />
                                        </ListItem>
                                        {i < Math.min(sortedProgress.length, 10) - 1 && <Divider />}
                                    </React.Fragment>
                                ))
                            )}
                        </List>
                    )}

                    {tabValue === 2 && (
                        <List sx={{ py: 0 }}>
                            {pastActivities.length === 0 ? (
                                <Box sx={{ py: 10, textAlign: 'center' }}>
                                    <Typography color="text.secondary" sx={{ fontWeight: 600 }}>No hay actividades finalizadas.</Typography>
                                </Box>
                            ) : (
                                pastActivities.map((e, i) => (
                                    <React.Fragment key={`${e.userId}-${e.activityId}-${i}`}>
                                        <ListItem sx={{ py: 2.5, px: 1 }}>
                                            <ListItemAvatar>
                                                <Avatar sx={{ bgcolor: 'secondary.light' }}><CheckCircleIcon /></Avatar>
                                            </ListItemAvatar>
                                            <ListItemText
                                                primary={<Typography variant="subtitle1" sx={{ fontWeight: 800 }}>{e.activityName}</Typography>}
                                                secondary={`Inscrito el: ${new Date(e.enrollmentDate).toLocaleDateString()} • Finalizó: ${new Date(e.activityEndDate || '').toLocaleDateString()}`}
                                            />
                                            <Chip label="Completado" color="success" size="small" sx={{ fontWeight: 700, borderRadius: 1.5 }} />
                                        </ListItem>
                                        {i < pastActivities.length - 1 && <Divider />}
                                    </React.Fragment>
                                ))
                            )}
                        </List>
                    )}
                </Box>
            </Paper>

            {/* Contenido oculto para exportar como PDF */}
            <Box
                id="pdf-report-content"
                sx={{ position: 'absolute', left: '-9999px', top: 0, width: '900px', bgcolor: 'white', p: 6 }}
            >
                {/* Header */}
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', mb: 4, pb: 3, borderBottom: '4px solid #5353EE' }}>
                    <Box>
                        <Typography variant="h3" sx={{ fontWeight: 950, color: '#5353EE', letterSpacing: '-2px' }}>ICESI GYM</Typography>
                        <Typography variant="h6" sx={{ fontWeight: 700, color: 'text.secondary', mt: 0.5 }}>Reporte de Rendimiento Personal</Typography>
                    </Box>
                    <Box sx={{ textAlign: 'right' }}>
                        <Typography variant="body1" sx={{ fontWeight: 700 }}>{user?.firstName} {user?.lastName}</Typography>
                        <Typography variant="body2" color="text.secondary">{user?.institutionalEmail}</Typography>
                        <Typography variant="caption" sx={{ color: '#5353EE', fontWeight: 800, display: 'block', mt: 0.5 }}>
                            GENERADO EL {new Date().toLocaleDateString().toUpperCase()}
                        </Typography>
                    </Box>
                </Box>

                {/* KPI Cards */}
                <Typography variant="overline" sx={{ fontWeight: 900, color: '#5353EE', letterSpacing: 3, display: 'block', mb: 2 }}>RESUMEN GENERAL</Typography>
                <Grid container spacing={2} sx={{ mb: 4 }}>
                    {[
                        { label: 'Sesiones Totales', value: stats.totalSessions, color: '#5353EE' },
                        { label: 'Peso Máximo', value: `${stats.maxWeight}kg`, color: '#9c27b0' },
                        { label: 'Días Activos', value: stats.activeDaysThisWeek, color: '#0288d1' },
                        { label: 'Reps Promedio', value: stats.avgReps, color: '#2e7d32' },
                    ].map((kpi) => (
                        <Grid size={{ xs: 3 }} key={kpi.label}>
                            <Box sx={{ bgcolor: kpi.color, color: 'white', borderRadius: 3, p: 2.5, textAlign: 'center' }}>
                                <Typography variant="h4" sx={{ fontWeight: 900 }}>{kpi.value}</Typography>
                                <Typography variant="caption" sx={{ fontWeight: 600, opacity: 0.9 }}>{kpi.label}</Typography>
                            </Box>
                        </Grid>
                    ))}
                </Grid>

                {/* Charts */}
                <Typography variant="overline" sx={{ fontWeight: 900, color: '#5353EE', letterSpacing: 3, display: 'block', mb: 2 }}>ANÁLISIS DE PROGRESO</Typography>
                <Grid container spacing={3} sx={{ mb: 4 }}>
                    <Grid size={{ xs: 6 }}>
                        <Box sx={{ border: '1px solid #eee', borderRadius: 3, p: 2 }}>
                            <SimpleBarChart data={stats.weeklyReps} title="Repeticiones por Día" subtitle="Últimos 7 días" unit="reps" />
                        </Box>
                    </Grid>
                    <Grid size={{ xs: 6 }}>
                        <Box sx={{ border: '1px solid #eee', borderRadius: 3, p: 2 }}>
                            <SimpleBarChart data={stats.monthlyVolume} title="Carga Semanal" subtitle="Volumen acumulado" color="secondary.main" unit="kg" />
                        </Box>
                    </Grid>
                </Grid>

                {/* Últimos registros */}
                <Typography variant="overline" sx={{ fontWeight: 900, color: '#5353EE', letterSpacing: 3, display: 'block', mb: 2 }}>ÚLTIMOS REGISTROS</Typography>
                <Box sx={{ border: '1px solid #eee', borderRadius: 3, overflow: 'hidden', mb: 4 }}>
                    {sortedProgress.slice(0, 8).map((p, i) => (
                        <Box key={p.idProgress} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', px: 3, py: 1.5, bgcolor: i % 2 === 0 ? '#fafafa' : 'white', borderBottom: i < 7 ? '1px solid #f0f0f0' : 'none' }}>
                            <Typography variant="body2" sx={{ fontWeight: 700, minWidth: 150 }}>{exerciseMap.get(p.exerciseId)?.name || 'Ejercicio'}</Typography>
                            <Typography variant="caption" color="text.secondary">{new Date(p.dateLogged).toLocaleString()}</Typography>
                            <Box sx={{ display: 'flex', gap: 1 }}>
                                <Chip label={`${p.reps} Reps`} size="small" sx={{ fontWeight: 700, bgcolor: '#e8f5e9', color: '#2e7d32' }} />
                                {p.weightKg && <Chip label={`${p.weightKg}kg`} size="small" sx={{ fontWeight: 700, bgcolor: '#e3f2fd', color: '#1565c0' }} />}
                            </Box>
                        </Box>
                    ))}
                </Box>

                {/* Footer */}
                <Box sx={{ pt: 2, borderTop: '1px solid #eee', display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="caption" color="text.secondary">© {new Date().getFullYear()} Icesi Gym</Typography>
                    <Typography variant="caption" color="text.secondary">Sistema de Gestión de Alto Rendimiento</Typography>
                </Box>
            </Box>

        </Box>
    );
};

export default HistoryScreen;
