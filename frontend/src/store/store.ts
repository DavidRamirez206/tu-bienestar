import { configureStore, combineReducers } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import uiReducer from './slices/uiSlice';
import realTimeReducer from './slices/realTimeSlice';

const rootReducer = combineReducers({
  auth: authReducer,
  ui: uiReducer,
  realTime: realTimeReducer,
});

export const store = configureStore({
  reducer: rootReducer,
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
