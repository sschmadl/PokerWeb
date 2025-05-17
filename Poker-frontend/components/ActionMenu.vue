<script setup lang="ts">
import { ref } from 'vue';
import { useGameSocket } from "~/stores/useGameSocket";
import { useUsername } from "~/composables/states";

const gameSocket = useGameSocket();
const selfUsername = useUsername().value;

let isTurn = ref<boolean>(false);

let currentPokerHand = 'Three of a Kind';

const relevantActions = [
  'BET', 'RAISE',
];
let actionHistory: string[] = [];

let checkCallAction = 'Check';
let betRaiseAction = 'Bet';

const showRaiseInput = ref(false);
const raiseAmount = ref<number | null>(null);

gameSocket.onMessage((data) => {
  switch (data.command) {
    case 'player-next-turn': {
      isTurn.value = data.name === selfUsername;
      break;
    }
    case 'player-action': {
      if (relevantActions.includes(data.action)) {
        actionHistory.push(data.action);
      }
      
      if (actionHistory.length > 0) {
        checkCallAction = 'Call';
        betRaiseAction = 'Raise';
      }
      break;
    }
    case 'new-betting-round': {
      revertValuesToDefault();
      break;
    }
  }
});

function revertValuesToDefault() {
  isTurn.value = false;
  checkCallAction = 'Check';
  betRaiseAction = 'Bet';
  actionHistory = [];
  showRaiseInput.value = false;
  raiseAmount.value = null;
}

function handleActionSend(action: string, amount?: number | null): void {
  const message = {
    command: 'player-action',
    action: action,
    amount: amount,
  }
  gameSocket.sendMessage(JSON.stringify(message))
}
</script>

<template>
  <Transition name="slide-left">
    <div
        v-if="isTurn"
        class="action-container p-10 bg-customPrimary-300 dark:bg-customPrimary-600 rounded-2xl max-w-xs mx-auto relative overflow-visible"
    >
      <div class="poker-hand-container text-2xl text-center mb-4">
        <strong>{{ currentPokerHand }}</strong>
      </div>
      <div class="button-container flex flex-col space-y-4">
        <UButton class="check-call-button text-black dark:text-white w-full" @click="handleActionSend(actionHistory.length > 0 ? 'CALL' : 'CHECK')">
          {{ checkCallAction }}
        </UButton>
        <UButton
            class="bet-raise-button text-black dark:text-white w-full"
            @click="showRaiseInput = true"
        >
          {{ betRaiseAction }}
        </UButton>
        <UButton class="fold-button text-black dark:text-white w-full" @click="handleActionSend('FOLD')">
          Fold
        </UButton>
      </div>
      
      <!-- Expanding Raise Input Box -->
      <Transition name="expand-right">
        <div
            v-if="showRaiseInput"
            class="raise-input-container absolute top-1/2 left-full ml-4 transform -translate-y-1/2 p-4 bg-white dark:bg-gray-800 rounded-2xl shadow-lg flex flex-col space-y-2 w-48 z-50"
        >
          <label class="text-sm text-black dark:text-white">Enter Raise Amount:</label>
          <input
              v-model.number="raiseAmount"
              type="number"
              class="w-full text-lg rounded-md border border-gray-300 dark:border-gray-600 p-2"
          />
          <div class="flex justify-between mt-2">
            <UButton
                class="confirm-raise-button text-black dark:text-white"
                @click="showRaiseInput = false; handleActionSend(actionHistory.length > 0 ? 'RAISE' : 'BET', raiseAmount)"
            >
              Confirm
            </UButton>
            <UButton
                class="cancel-raise-button text-black dark:text-white"
                @click="showRaiseInput = false"
            >
              Cancel
            </UButton>
          </div>
        </div>
      </Transition>
    </div>
  </Transition>
</template>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active {
  transition: transform 0.4s ease, opacity 0.4s ease;
}
.slide-left-enter-from,
.slide-left-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}
.slide-left-enter-to,
.slide-left-leave-from {
  transform: translateX(0);
  opacity: 1;
}

.expand-right-enter-active,
.expand-right-leave-active {
  transition: all 0.3s ease;
}
.expand-right-enter-from,
.expand-right-leave-to {
  opacity: 0;
  transform-origin: left;
  top: 50%;
  transform: translateY(-50%) scaleX(0);
}
.expand-right-enter-to,
.expand-right-leave-from {
  opacity: 1;
  transform-origin: left;
  top: 50%;
  transform: translateY(-50%) scaleX(1);
}

</style>
