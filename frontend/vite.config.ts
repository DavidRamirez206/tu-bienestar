import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // El archivo .env es unico y vive en la raiz del repositorio
  envDir: '..',
  server: {
    port: 6573,
  },
});
