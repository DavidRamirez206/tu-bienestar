import React from 'react';
import { Box, Typography, Tooltip } from '@mui/material';

type DataPoint = {
  label: string;
  value: number;
};

type Props = {
  data: DataPoint[];
  title: string;
  subtitle?: string;
  height?: number;
  color?: string;
  unit?: string;
};

const SimpleBarChart: React.FC<Props> = ({ data, title, subtitle, height = 180, color = 'primary.main', unit = '' }) => {
  const maxValue = Math.max(...data.map(d => d.value), 1);
  const barWidth = 35;
  const gap = 15;

  return (
    <Box sx={{ p: 1, textAlign: 'left' }}>
      <Typography variant="subtitle1" sx={{ fontWeight: 800, color: 'text.primary', mb: 0.5 }}>
        {title}
      </Typography>
      {subtitle && (
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 3, fontWeight: 500 }}>
              {subtitle}
          </Typography>
      )}
      
      <Box sx={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'flex-end', 
          height, 
          gap: `${gap}px`, 
          borderBottom: '1px solid', 
          borderColor: 'divider', 
          pb: 1,
          px: 1 
      }}>
        {data.map((d, i) => {
          const barHeight = maxValue > 0 ? (d.value / maxValue) * (height - 30) : 0;
          return (
            <Tooltip key={i} title={`${d.value} ${unit}`} arrow placement="top">
                <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: barWidth, flexGrow: 1 }}>
                <Box 
                    sx={{ 
                    width: '100%', 
                    height: Math.max(barHeight, 4), 
                    bgcolor: d.value > 0 ? color : 'grey.100', 
                    borderRadius: '6px 6px 0 0',
                    transition: 'all 0.6s cubic-bezier(0.4, 0, 0.2, 1)',
                    cursor: 'pointer',
                    '&:hover': { 
                        filter: 'brightness(1.2)',
                        transform: 'scaleX(1.05)'
                    },
                    '@media print': {
                        transition: 'none !important',
                        transform: 'none !important',
                        cursor: 'default !important',
                        border: '1px solid #ddd'
                    }
                    }} 
                />
                <Typography variant="caption" sx={{ mt: 1, whiteSpace: 'nowrap', fontWeight: 700, fontSize: '0.65rem', color: 'text.secondary' }}>
                    {d.label}
                </Typography>
                </Box>
            </Tooltip>
          );
        })}
      </Box>
    </Box>
  );
};

export default SimpleBarChart;
