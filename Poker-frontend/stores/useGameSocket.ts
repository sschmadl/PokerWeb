import { defineStore } from 'pinia';

export const useGameSocket = defineStore('gameSocket', () => {
    let socket: WebSocket | null = null;
    const token = useJWT().value;

    const isConnected = ref(false);
    const lastMessage = ref<string | null>(null);

    function connect() {
        if (socket) return; // prevent re-connecting

        socket = new WebSocket(`ws://${useRequestURL().host}/ws/game-info-socket?token=${token}`);

        socket.onopen = () => {
            isConnected.value = true;
            console.log('WebSocket connected');
        };

        socket.onmessage = (event) => {
            lastMessage.value = event.data;
            console.log('Received:', event.data);
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

    function waitForMessageOnce(): Promise<string> {
        return new Promise((resolve) => {
            const handler = (event: MessageEvent) => {
                resolve(event.data);
                socket?.removeEventListener('message', handler); // One-time use
            };
            socket?.addEventListener('message', handler);
        });
    }

    function disconnect() {
        socket?.close();
        socket = null;
    }

    return {
        isConnected,
        lastMessage,
        connect,
        disconnect,
        sendMessage,
        waitForMessageOnce,
    };
});
