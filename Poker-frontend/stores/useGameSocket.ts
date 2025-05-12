import { defineStore } from 'pinia';
import { useToast } from '#imports';

export const useGameSocket = defineStore('gameSocket', () => {
    let socket: WebSocket | null = null;
    const token = useJWT().value;

    const isConnected = ref(false);
    const lastMessage = ref<string | null>(null);

    let messageListeners: Array<(data: any) => void> = [];

    function connect() {
        if (socket) return; // prevent re-connecting
        console.log('Token: ', token);
        socket = new WebSocket(`ws://${useRequestURL().host}/ws/game-info-socket?token=${token}`);

        socket.onopen = () => {
            isConnected.value = true;
            console.log('WebSocket connected');
        };

        socket.onmessage = (event) => {
            lastMessage.value = event.data;

            const parsed = safeJsonParse(event.data);

            if (parsed.command === "server-message") {
                const toast = useToast();
                toast.add({
                    title: parsed.title,
                    description: parsed.message,
                    color: parsed.color,
                });
            }

            // Notify all listeners
            messageListeners.forEach((listener) => listener(parsed));
        };

        socket.onclose = () => {
            isConnected.value = false;
            socket = null;
            console.log('WebSocket closed');
        };
    }

    function sendMessage(message: string) {
        if (socket && isConnected.value) {
            socket.send(message);
        }
    }

    function waitForMessageOnce(): Promise<any> {
        return new Promise((resolve) => {
            const handler = (event: MessageEvent) => {
                socket?.removeEventListener('message', handler);
                resolve(safeJsonParse(event.data));
            };
            socket?.addEventListener('message', handler);
        });
    }

    function disconnect() {
        socket?.close();
        socket = null;
    }

    function onMessage(callback: (data: any) => void) {
        messageListeners.push(callback);
    }

    function safeJsonParse(data: any) {
        try {
            return JSON.parse(data);
        } catch {
            return data;
        }
    }

    return {
        isConnected,
        lastMessage,
        connect,
        disconnect,
        sendMessage,
        waitForMessageOnce,
        onMessage,
    };
});
