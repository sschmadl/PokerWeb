<script setup lang="ts">
import {type InferType, number, object, string} from "yup";

type LobbyItems = {
  gameId: string;
  name: string;
  playerCount: string;
  smallBlind: string;
  bigBlind: string;
};

const schema = object({
  playerCount: number().required('Required'),
  smallBlind: number().required('Required'),
  bigBlind: number().required('Required'),
})

type Schema = InferType<typeof schema>

const state = reactive({
  playerCount: 4,  // Default value
  smallBlind: 2,
  bigBlind: 4,
});

const smallBlindMax = 1000

function calculateBigBlind() {
  state.bigBlind = state.smallBlind * 2;
  return state.bigBlind;
}

const filterInput = (event: KeyboardEvent) => {
  // Allow numbers and backspace key
  if (!/\d/.test(event.key) && event.key !== 'Backspace') {
    event.preventDefault();
    return;
  }

  // Get the current input and simulate adding the pressed key
  const newValue = Number(state.smallBlind + event.key);

  // Prevent input if the value will go past the max or set to 0
  if (newValue > smallBlindMax || newValue === 0) {
    event.preventDefault();
  }
};


const items = [
  {
    label: "Find existing game",
    key: "find-game",
    icon: "",
    buttonText: "Join game",
  },
  {
    label: "Create new game",
    key: "create-game",
    icon: "",
    buttonText: "Create game",
  },
];

const gameTableRows: LobbyItems[] = [
  {
    gameId: "wgpor034nj",
    name: "XirouMMMMMMMMMM's game",
    playerCount: "2/6",
    smallBlind: "5€",
    bigBlind: "10€",
  },
  {
    gameId: "wgkjadfsnj",
    name: "Günther's game",
    playerCount: "4/7",
    smallBlind: "50€",
    bigBlind: "100€",
  },
];

const gameTableColumns = [
  {key: "name", label: "Name"},
  {key: "playerCount", label: "Players"},
  {key: "smallBlind", label: "Small Blind"},
  {key: "bigBlind", label: "Big Blind"},
];

const selected = ref<LobbyItems | null>(null);

function onSelect(row: LobbyItems) {
  selected.value = row;
}


function submit(tabType: string) {
  if (tabType === 'find-game') {
    console.log(selected.value);
    navigateTo('/poker-game');
  } else if (tabType === 'create-game') {
    if (state.smallBlind > 0) {
      // @Todo Simon den schaß geben
      navigateTo('/poker-game');
    }
  }
}
</script>

<template>
  <NavBar/>
  <UContainer class="w-1/2">
    <UTabs :items="items">
      <template #item="{ item }">
        <UCard class="align-items-center text-center">
          <template #header>
            <p class="text-base font-semibold leading-6 text-gray-900 dark:text-white">
              {{ item.label }}
            </p>
            <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
              {{ item.description }}
            </p>
          </template>

          <div v-if="item.key === 'find-game'" class="space-y-3">
            <div class="current-games-header">
              <span v-for="(row, index) in gameTableColumns" :key="index" class="current-games-header-text">
                {{ row.label }}
              </span>
            </div>

            <div
                v-for="(row, rowIndex) in gameTableRows"
                :key="rowIndex"
                @click="onSelect(row)"
                class="current-games flex px-4 py-2 rounded cursor-pointer transition-colors duration-200"
                :class="selected?.name === row.name
                  ? 'bg-gray-200 dark:bg-gray-600'
                  : 'hover:bg-gray-100 dark:hover:bg-gray-700'"
            >
              <span class="current-games-text flex-1 text-center text-sm text-gray-800 dark:text-gray-200">
                {{ row.name }}
              </span>
              <span class="current-games-text flex-1 text-center text-sm text-gray-800 dark:text-gray-200">
                {{ row.playerCount }}
              </span>
              <span class="current-games-text flex-1 text-center text-sm text-gray-800 dark:text-gray-200">
                {{ row.smallBlind }}
              </span>
              <span class="current-games-text flex-1 text-center text-sm text-gray-800 dark:text-gray-200">
                {{ row.bigBlind }}
              </span>
            </div>


          </div>
          <div v-else-if="item.key === 'create-game'" class="space-y-3">
            <UForm :schema="schema" :state="state">
              <p class="text-left">Player Count: {{ state.playerCount }}</p>
              <URange v-model="state.playerCount" :min="2" :max="10" :default-value="4"/>
              <p class="text-left">Small Blind:</p>
              <UInput v-model="state.smallBlind" type="number" placeholder="Enter a number (Max: 1000 €)" :min="1"
                      :max="100" @keydown="filterInput"></UInput>
              <p class="text-left">Big Blind:</p>
              <p class="text-left">{{ calculateBigBlind() }} €</p>
            </UForm>
          </div>

          <template #footer>
            <UButton type="submit" @click="submit(item.key)">
              {{ item.buttonText }}
            </UButton>
          </template>
        </UCard>
      </template>
    </UTabs>
  </UContainer>
</template>

<style scoped>
.current-games-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-games {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-games-header-text {
  font-size: x-large;
  flex: 1;
  text-align: center;
}

.current-games-text {
  flex: 1;
  text-align: center;
}

</style>
