<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { useUsername } from "~/composables/states";
import { useGameSocket } from "~/stores/useGameSocket"; // Adjust path if needed

// Chat state
const messageInput = ref('');
const messages = ref<{ username: string, message: string }[]>([]);
const chatMessagesRef = ref<HTMLElement | null>(null);

// Game socket store
const gameSocket = useGameSocket();

// Scroll helper
const isNearBottom = () => {
  const container = chatMessagesRef.value;
  if (!container) return false;
  const threshold = 30;
  const distanceFromBottom = container.scrollHeight - container.scrollTop - container.clientHeight;
  return distanceFromBottom <= threshold;
};

// Send message to WebSocket
const sendMessage = async () => {
  if (!messageInput.value.trim()) return;

  const shouldAutoScroll = isNearBottom();

  const payload = {
    command: 'chat-message',
    sender: useUsername().value,
    message: messageInput.value,
  };

  console.log('Sending message:', payload);
  gameSocket.sendMessage(JSON.stringify(payload));

  messageInput.value = '';

  await nextTick();

  if (shouldAutoScroll && chatMessagesRef.value) {
    chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight;
  }
};

// Handle messages received from WebSocket
const handleIncomingMessage = async (data: any) => {
  console.log('Received WebSocket data:', data);
  console.log(data);
  if (data.command === 'chat-message') {
    console.log('New chat message received:', data);

    messages.value.push({
      username: data.sender,
      message: data.message,
    });

    await nextTick();
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight;
    }


  } else {
    console.log('Unknown command received:', data.command);
  }
};

// Lifecycle hooks
onMounted(() => {
  console.log('Chat component mounted. Connecting WebSocket...');
  gameSocket.connect();
  gameSocket.onMessage(handleIncomingMessage);
});
</script>




<template>
  <div class="chat-container">
    <!-- Display all chat messages -->
    <div ref="chatMessagesRef" class="chat-messages">
      <div v-for="(msg, index) in messages" :key="index" class="chat-message">
        <span class="username">{{ msg.username }} : </span>
        <span class="message">{{ msg.message }}</span>
      </div>
    </div>

    <!-- Input field for the user to type a message -->
    <div class="input-area">
      <input
          v-model="messageInput"
          type="text"
          placeholder="Type a message..."
          class="message-input"
          @keydown.enter="sendMessage"
      />
      <button @click="sendMessage" class="send-button">Send</button>
    </div>
  </div>
</template>


<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  width: 300px;
  max-height: 500px;
  background-color: rgba(0, 0, 0, 0.6);
  padding: 10px;
  position: absolute;
  bottom: 2%;
  right: 10px;
  border-radius: 8px;
  overflow-y: auto;
}

.chat-messages {
  width: 100%;
  max-height: 90%;
  overflow-y: auto;
  margin-bottom: 10px;
}

.chat-message {
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 8px;
  padding: 8px;
  margin-bottom: 8px;
  word-wrap: break-word;
}

.username {
  font-weight: bold;
  color: #4caf50; /* Color for the username */
}

.message {
  color: #fff;
}

.input-area {
  display: flex;
  width: 100%;
  justify-content: space-between;
}

.message-input {
  width: 80%;
  padding: 8px;
  margin-right: 10px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.send-button {
  padding: 8px 16px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.send-button:hover {
  background-color: #45a049;
}
</style>
