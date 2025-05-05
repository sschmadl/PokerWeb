<script setup lang="ts">
import { ref, nextTick } from 'vue';
import { useUsername } from "~/composables/states";

// State for the chat input and messages
const messageInput = ref('');
const messages = ref<{ username: string, message: string }[]>([]);

// Ref for the chat messages container
const chatMessagesRef = ref<HTMLElement | null>(null);

// Function to check if the scroll is near the bottom
const isNearBottom = () => {
  const container = chatMessagesRef.value;
  if (!container) return false;

  const threshold = 30; // px from bottom
  const distanceFromBottom = container.scrollHeight - container.scrollTop - container.clientHeight;
  return distanceFromBottom <= threshold;
};

// Function to send a message
const sendMessage = async () => {
  if (messageInput.value.trim()) {
    const shouldAutoScroll = isNearBottom(); // Check before DOM updates

    messages.value.push({ username: useUsername().value, message: messageInput.value });
    messageInput.value = '';

    await nextTick();

    if (shouldAutoScroll && chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight;
    }
  }
};
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
  max-height: 80vh;
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
  max-height: 200px;
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
