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
    default: '/default_profile_picture.jpg'
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
    default: false
  },

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
const personalCardHeight = computed(() => (userNameContainerHeight.value + pokerhandContainerHeight.value) * 1.25);


</script>

<template>
  <div class="player-info-wrapper flex flex-col items-center w-auto">
    <!-- Profile & Cards Layout -->
    <div
        class="menu-container"
        :class="{ 'highlighted-menu': highlighted }"
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
        marginLeft: profilePictureDiameter + profilePictureTextMargin / 3 + 'px',
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

        <!-- Action Section -->
        <div
            v-if="playerAction && playerAction.trim() !== ''"
            class="player-action-bar bg-yellow-300 dark:bg-yellow-500 text-black dark:text-black"
            :style="{
              width: informationContainerWidth + 'px',
              fontSize: Math.floor(menuWidth * 0.04) + 'px',
              padding: '2px 6px',
              textAlign: 'left',
              borderRadius: '0 0 6px 6px',
              marginTop: '-2px',
            }"
        >
          {{ playerAction }}
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

.highlighted-menu {
  box-shadow: 0 0 1000px 4px rgba(255, 215, 0, 0.7); /* Gold glow effect */
  border-radius: 12px;
  transition: box-shadow 0.3s ease;
}

</style>
