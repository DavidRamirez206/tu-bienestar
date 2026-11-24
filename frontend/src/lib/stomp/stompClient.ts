import { Client } from '@stomp/stompjs';
import type { IMessage } from '@stomp/stompjs';

export interface SubscriptionConfig {
  destination: string;
  onMessage: (body: unknown) => void;
}

/**
 * URL del WebSocket. Vive bajo /api para que el navegador envie la cookie de sesion en el
 * handshake (la cookie solo aplica a /api), igual que en las peticiones REST.
 */
export const buildBrokerUrl = (): string => {
  const apiHost = import.meta.env.VITE_API_HOST as string | undefined;
  const base = apiHost && apiHost !== '/' ? new URL(apiHost) : new URL(window.location.origin);
  base.protocol = base.protocol === 'https:' ? 'wss:' : 'ws:';
  base.pathname = '/api/ws';
  return base.toString();
};

export default function createStompClient(
  subscriptions: SubscriptionConfig[],
  onConnect?: () => void,
): Client {
  const client = new Client({
    brokerURL: buildBrokerUrl(),
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
  });

  client.onConnect = () => {
    subscriptions.forEach(({ destination, onMessage }) => {
      client.subscribe(destination, (message: IMessage) => {
        try {
          onMessage(JSON.parse(message.body));
        } catch {
          // Un mensaje mal formado no debe tumbar la conexion.
        }
      });
    });
    onConnect?.();
  };

  return client;
}
