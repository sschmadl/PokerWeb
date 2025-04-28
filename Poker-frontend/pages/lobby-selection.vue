<script setup lang="ts">
import {number, object} from "yup";
import {useGameSocket} from "~/stores/useGameSocket";

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
});

const state = reactive({
  gameName: '',
  playerCount: 4,
  smallBlind: 2,
  bigBlind: 4,
});

const gameNameMaxCharLength = 20;
const smallBlindMax = 1000;

function calculateBigBlind() {
  state.bigBlind = state.smallBlind * 2;
  return state.bigBlind;
}

const filterInput = (event: KeyboardEvent) => {
  if (!/\d/.test(event.key) && event.key !== 'Backspace') {
    event.preventDefault();
    return;
  }

  const newValue = Number(state.smallBlind + event.key);

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

const gameTableRows = ref<LobbyItems[]>([
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
]);

const gameTableColumns = [
  { key: "name", label: "Name" },
  { key: "playerCount", label: "Players" },
  { key: "smallBlind", label: "Small Blind" },
  { key: "bigBlind", label: "Big Blind" },
];

async function fetchExistingGames() {
  try {
    const response = await $fetch<LobbyItems[]>("/game-info/existing-games", {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    gameTableRows.value = response.games;
    console.log(response.games);

  } catch (error: any) {
    if (error.data?.error) {
      console.log(error.data?.error);
    } else {
      console.log("Geh scheißn");
    }
  }
}

const selected = ref<LobbyItems | null>(null);

function onSelect(row: LobbyItems) {
  selected.value = row;
}

await fetchExistingGames();

const gameSocket = useGameSocket();

onMounted(() => {
  gameSocket.connect();
});

onBeforeUnmount(() => {
  // gameSocket.disconnect();
});

async function submit(tabType: string) {
  if (tabType === "find-game") {
    const message = {
      command: 'join-game',
      gameId: selected.value?.gameId,
    }

    gameSocket.sendMessage(JSON.stringify(message));
    try {
      const response = await gameSocket.waitForMessageOnce();
      if (response === 'success') {
        navigateTo("/poker-game");
      } else {
        console.error("Failed to join game");
      }
    } catch (e) {
      console.error("Failed to receive server response", e);
    }
  } else if (tabType === "create-game") {
    const message = {
      command: 'create-game',
      gameName: state.gameName,
      smallBlind: state.smallBlind,
      bigBlind: state.bigBlind,
      maxPlayerCount: state.playerCount,
    }
    console.log(message);

    gameSocket.sendMessage(JSON.stringify(message));
    try {
      const response = await gameSocket.waitForMessageOnce();
      console.log("Create game response:", response);

      if (response === 'success') {
        navigateTo("/poker-game");
      } else {
        console.error("Failed to create game");
      }
    } catch (e) {
      console.error("Failed to receive server response", e);
    }
  }
}
</script>

<template>
  <NavBar />
  <UContainer class="w-3/4">
    <UTabs :items="items">
      <template #item="{ item }">
        <UCard class="align-items-center text-center w-full">
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
                v-for="(row, rowIndex) in gameTableRows.values()"
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
              <UFormGroup name="gameName" class="pt-4">
                <p class="text-left">Game - Name</p>
                <UInput
                    v-model="state.gameName"
                    :maxlength="gameNameMaxCharLength"
                    aria-describedby="character-count"
                >
                  <template #trailing>
                    <div
                        id="character-count"
                        class="text-xs text-gray-500 tabular-nums"
                        aria-live="polite"
                        role="status"
                    >
                      {{ state.gameName.length }}/{{ gameNameMaxCharLength }}
                    </div>
                  </template>
                </UInput>
              </UFormGroup>
              <p class="text-left">Player Count: {{ state.playerCount }}</p>
              <URange v-model="state.playerCount" :min="2" :max="10" :default-value="4" />
              <p class="text-left">Small Blind:</p>
              <UInput
                  v-model="state.smallBlind"
                  type="number"
                  placeholder="Enter a number (Max: 1000 €)"
                  :min="1"
                  :max="100"
                  @keydown="filterInput"
              ></UInput>
              <p class="text-left">Big Blind:</p>
              <p class="text-left">{{ calculateBigBlind() }} €</p>
            </UForm>
          </div>

          <template #footer>
            <div class="relative w-full flex justify-center">
              <UButton type="submit" @click="submit(item.key)">
                {{ item.buttonText }}
              </UButton>

              <div v-if="item.key === 'find-game'" class="absolute right-0">
                <UButton class="rounded-full" :icon="'material-symbols:refresh-rounded'" @click="fetchExistingGames" />
              </div>
            </div>
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
