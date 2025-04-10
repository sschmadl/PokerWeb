<script setup lang="ts">
import { ref } from 'vue';

// State for the chat input and messages
const messageInput = ref(''); // The message the user types
const messages = ref<{ username: string, message: string }[]>([]); // Array to store chat messages

// Function to send a message
const sendMessage = () => {
  if (messageInput.value.trim()) {
    messages.value.push({ username: 'Player', message: messageInput.value });
    messageInput.value = ''; // Clear the input after sending
  }
};
</script>

<template>
  <div class="chat-container">
    <!-- Display all chat messages -->
    <div class="chat-messages">
      <div v-for="(msg, index) in messages" :key="index" class="chat-message">
        <span class="username">{{ msg.username }}:</span>
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
