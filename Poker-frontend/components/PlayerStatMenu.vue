<script setup lang="ts">

import type {Card} from "~/pages/poker-game.vue";

const props = defineProps({
  menuWidth: {
    type: Number,
    required: true,
  },
  profileBorderColor: String,
  cards: {
    type: Array as PropType<Array<Partial<Card>>>,
    default: () => [
      {frontImage: '/cards_default/1J.svg', faceDown: true, highlighted: false},
      {frontImage: '/cards_default/2J.svg', faceDown: true, highlighted: false},
    ]
  },
  profilePicture: {
    type: String,
  },
  playerName: {
    type: String,
    default: '',
  },
  playerMoney: {
    type: Number,
    default: 0,
  },
  playerAction: {
    type: String,
    default: '',
  },
  highlighted: {
    type: Boolean,
    default: false,
  },
  isAdmin: {
    type: Boolean,
    default: false,
  }
});


const cacheBustedProfilePicture = computed(() => {
  return props.profilePicture + '?t=' + Date.now();
});

const playerName = computed(() => props.playerName);
const playerMoney = computed(() => props.playerMoney);
const playerAction = computed(() => props.playerAction);
const cards = computed(() => {
  return props.cards?.length ? props.cards : [
    {frontImage: '/cards_default/1J.svg', faceDown: true, highlighted: false},
    {frontImage: '/cards_default/2J.svg', faceDown: true, highlighted: false},
  ];
});
const highlighted = computed(() => props.highlighted);
const isAdmin = computed(() => props.isAdmin);
const profilePictureDiameter = computed(() => Math.floor(props.menuWidth * 0.4));
const profilePictureTextMargin = computed(() => profilePictureDiameter.value * 0.2);
const profilePictureMenuPadding = computed(() => profilePictureDiameter.value - profilePictureTextMargin.value + profilePictureDiameter.value * 0.05);
const profileBorderWidth = computed(() => Math.floor(props.menuWidth / 100));
const profileBorderColor = computed(() => props.profileBorderColor ?? '#000');
const informationContainerWidth = computed(() => props.menuWidth - profilePictureDiameter.value);
const userNameContainerHeight = computed(() => Math.ceil(props.menuWidth * 0.135));
const userNameContainerFontSize = computed(() => Math.floor(userNameContainerHeight.value * 0.5));
const pokerhandContainerHeight = computed(() => Math.ceil(props.menuWidth * 0.155));
const pokerhandContainerFontSize = computed(() => Math.floor(pokerhandContainerHeight.value * 0.5));
const roundEdgeCircleDiameter = computed(() => pokerhandContainerHeight.value + userNameContainerHeight.value);
const personalCardHeight = computed(() => (userNameContainerHeight.value + pokerhandContainerHeight.value) * 1.5);


</script>

<template>
  <div class="player-info-wrapper flex flex-col items-center w-auto">
    <!-- Profile & Cards Layout -->
    <div
        class="menu-container"
        :style="{ width: menuWidth + 'px' }"
    >
      <!-- Profile picture (Adjust position) -->
      <div class="player-profile-picture-container"
           :style="{ position: 'absolute', zIndex: 5, top: menuWidth/3 + 'px', left: menuWidth/50 + 'px' }">
        <img
            :src="cacheBustedProfilePicture"
            class="profile-picture"
            :style="{
            width: profilePictureDiameter + 'px',
            height: profilePictureDiameter + 'px',
            borderRadius: '100%',
            border: profileBorderWidth + 'px solid ' + profileBorderColor,
          }"
            alt="Profile Picture"
        />
      </div>
      
      <!-- Cards -->
      <div class="player-cards-container" :style="{
        position: 'absolute',
        width: informationContainerWidth + 'px',
        zIndex: -5,
        display: 'inline-flex',
        marginLeft: profilePictureDiameter * 0.85 + 'px',
        marginTop: profilePictureDiameter / 1.5, /* Adjust margin to avoid overlap */
      }">
        <Card
            v-for="(card, index) in cards"
            :key="index"
            :face-down="card.faceDown"
            :front-image="card.frontImage"
            :height="personalCardHeight"
            :highlighted="card.highlighted"
        />
      </div>
      
      <div class="player-stat-container" :style="{
        width: informationContainerWidth + 'px',
        paddingLeft: profilePictureMenuPadding + 'px',
        zIndex: 1,
        marginTop: profilePictureDiameter + 'px',
      }">
        <!-- Username section -->
        <div class="player-username-container bg-gray-400 dark:bg-gray-700" :style="{
          width: informationContainerWidth + 'px',
          height: userNameContainerHeight + 'px',
          fontSize: userNameContainerFontSize + 'px',
        }">
          <span class="username-text" :style="{
            marginLeft: profilePictureTextMargin + 'px',
          }">{{ playerName }}</span>
          <div class="round-end bg-gray-400 dark:bg-gray-700" :style="{
            width: roundEdgeCircleDiameter + 'px',
            height: roundEdgeCircleDiameter + 'px',
            clipPath: 'inset(0 0 ' + pokerhandContainerHeight + 'px 0)',
            transform: 'translateX(-50%)',
            marginTop: pokerhandContainerHeight + 'px',
          }"></div>
        </div>
        
        <!-- Money Section -->
        <div class="player-pokerhand-money-container bg-gray-500 dark:bg-gray-600" :style="{
          width: informationContainerWidth + 'px',
          height: pokerhandContainerHeight + 'px',
          fontSize: pokerhandContainerFontSize + 'px',
        }">
          <span :style="{
            marginLeft: profilePictureTextMargin + 'px',
          }">{{ playerMoney }}</span>
          <div class="round-end bg-gray-500 dark:bg-gray-600" :style="{
            width: roundEdgeCircleDiameter + 'px',
            height: roundEdgeCircleDiameter + 'px',
            clipPath: 'inset(' + userNameContainerHeight + 'px 0 0 0)',
            transform: 'translateX(-50%)',
            marginBottom: (userNameContainerHeight) + 'px',
          }"></div>
        </div>
        
        <!-- Action + Turn Section Side-by-Side -->
        <div class="flex justify-between w-full" :style="{ width: informationContainerWidth + 'px' }">
          <!-- Action Section (Left aligned) -->
          <div
              v-show="playerAction && playerAction.trim() !== ''"
              class="player-action-bar bg-yellow-300 dark:bg-yellow-600 text-black dark:text-white"
              :style="{
                width: '69%',
                fontSize: Math.floor(menuWidth * 0.04) + 'px',
                padding: '2px 6px',
                textAlign: 'center',
                borderRadius: '0 0 6px 6px',
                marginTop: '-2px',
                visibility: playerAction && playerAction.trim() !== '' ? 'visible' : 'hidden'
              }"
          >
            {{ playerAction }}
          </div>
          
          <!-- Turn Section (Right aligned) -->
          <div
              v-show="true"
              class="player-action-bar bg-purple-300 dark:bg-purple-600 text-black dark:text-white"
              :style="{
                width: '29%',
                fontSize: Math.floor(menuWidth * 0.04) + 'px',
                padding: '2px 6px',
                textAlign: 'center',
                borderRadius: '0 0 6px 6px',
                marginTop: '-2px',
                visibility: highlighted ? 'visible' : 'hidden'
              }"
          >
            Turn
          </div>
        
        </div>
        
        <div
            class="player-crown-container"
            v-if="isAdmin"
            :style="{
    position: 'absolute',
    top: (menuWidth / 3 - profilePictureDiameter * 0.4) + 'px',
    left: menuWidth / 50 + profilePictureDiameter * 0.25 + 'px',
    zIndex: 6,
    width: profilePictureDiameter * 0.5 + 'px',
    height: profilePictureDiameter * 0.5 + 'px',
  }"
        >
          <img
              src="/Crown.svg"
              alt="Crown"
              style="width: 100%; height: 100%; object-fit: contain"
          />
        </div>
      </div>
    
    </div>
  
  </div>
</template>

<style scoped>
.player-username-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.username-text {
  display: block;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 100%;
  line-height: 1.4;
}


.player-pokerhand-money-container {
  position: relative;
  display: flex;
  align-items: center;
  text-align: left;
}

.round-end {
  position: absolute;
  border-radius: 50%;
  left: 100%;
  z-index: -1;
}

.menu-container {
  position: relative;
  display: flex;
  align-items: center;
}

</style>
