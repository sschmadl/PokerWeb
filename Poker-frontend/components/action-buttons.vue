<script setup lang="ts">
import { ref } from 'vue';

const Check = () => {
  console.log("Check");
};

const Fold = () => {
  console.log("Fold");
};

const Raise = () => {
  console.log("Raise");
  showInput.value = true; // Show the input field when "Raise" is clicked
};

const ConfirmRaise = () => {
  // Check if the input is a valid number and greater than 0
  if (!raiseAmount.value || raiseAmount.value <= 0 || isNaN(raiseAmount.value)) {
    alert("Please enter a valid raise amount");
    return; // Don't proceed if invalid input
  }

  console.log(`Raise confirmed with amount: ${raiseAmount.value}`);
  // Add any logic for submitting the raise, e.g., sending to an API or updating the game state.

  showInput.value = false; // Hide the input field after confirmation
};

const showInput = ref(false); // State to show/hide the input field
const raiseAmount = ref<number | null>(null); // State to hold the raised amount
</script>

<template>
  <div class="min-h-screen flex items-end justify-center px-[20vw]">
    <UCard class="w-full text-center">
      <h2 class="text-xl font-semibold mb-4">Choose an Option</h2>

      <div class="flex justify-center gap-4 mb-4">
        <UButton @click="Check" size="xl" class="centered-button">Check</UButton>
        <UButton @click="Fold" size="xl" class="centered-button">Fold</UButton>
        <UButton @click="Raise" size="xl" class="centered-button">Raise</UButton>
      </div>

      <!-- Conditional rendering for the raise amount input field -->
      <div v-if="showInput" class="mt-4">
        <input
            v-model="raiseAmount"
            type="number"
            placeholder="Enter raise amount"
            class="border p-2 rounded-lg"
        />
      </div>

      <!-- Confirm button appears only when input field is visible -->
      <div v-if="showInput" class="mt-4">
        <UButton @click="ConfirmRaise" size="xl" class="centered-button">Confirm Raise</UButton>
      </div>
    </UCard>
  </div>
</template>

<style scoped>

.centered-button {
  color: white;
  background-color: darkcyan;
  width: 150px;

  justify-content: center;
  align-items: center;
}
</style>