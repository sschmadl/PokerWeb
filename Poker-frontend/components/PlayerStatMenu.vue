<script setup lang="ts">

const props = defineProps({
  menuWidth: {
    type: Number,
    required: true,
  },
  profileBorderColor: String,
  cards: {
    type: Array as PropType<Array<{ frontImage: string; faceDown: boolean; highlighted: boolean; }>>,
    required: true,
  },
});


// Colors
const profilePictureDiameter = Math.floor(props.menuWidth * 0.4);
const profilePictureTextMargin = profilePictureDiameter * 0.2;
const profilePictureMenuPadding = profilePictureDiameter - profilePictureTextMargin + profilePictureDiameter * 0.05;
const profileBorderWidth = Math.floor(props.menuWidth / 100);
const profileBorderColor = props.profileBorderColor !== undefined ? props.profileBorderColor : '#000';

const informationContainerWidth = props.menuWidth - profilePictureDiameter;
const userNameContainerHeight = Math.ceil(props.menuWidth * 0.135);
const userNameContainerFontSize = Math.floor(userNameContainerHeight * 0.5);

const pokerhandContainerHeight = Math.ceil(props.menuWidth * 0.155);
const pokerhandContainerFontSize = Math.floor(pokerhandContainerHeight * 0.5);

const roundEdgeCircleDiameter = pokerhandContainerHeight + userNameContainerHeight;

const personalCardHeight = (userNameContainerHeight + pokerhandContainerHeight) * 1.25;

</script>

<template>
  <div class="menu-container" :style="{
    width: menuWidth + 'px',
  }">
    <div class="player-profile-picture-container" :style="{
      position: 'absolute',
      zIndex: 5,
    }">
      <img src="/test_profile.jpg" class="profile-picture" :style="{
        width: profilePictureDiameter + 'px',
        height: profilePictureDiameter + 'px',
        borderRadius: '100%',
        border: profileBorderWidth + 'px solid ' + profileBorderColor,
      }" alt="Profile Picture"/>
    </div>
    <div class="player-cards-container" :style="{
      position: 'absolute',
      width: informationContainerWidth + 'px',
      zIndex: -5,
      display: 'inline-flex',
      marginLeft: profilePictureDiameter + profilePictureTextMargin / 3 + 'px',
      marginBottom: personalCardHeight + 'px',
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
    }">
      <div class="player-username-container bg-gray-400 dark:bg-gray-700" :style="{
        width: informationContainerWidth + 'px',
        height: userNameContainerHeight + 'px',
        fontSize: userNameContainerFontSize + 'px',
      }">
        <span class="username-text" :style="{
          marginLeft: profilePictureTextMargin + 'px',
        }">Daniel Rewobourggggggggggggg</span>
        <div class="round-end bg-gray-400 dark:bg-gray-700" :style="{
          width: roundEdgeCircleDiameter + 'px',
          height: roundEdgeCircleDiameter + 'px',
          clipPath: 'inset(0 0 ' + pokerhandContainerHeight + 'px 0)',
          transform: 'translateX(-50%)',
          marginTop: pokerhandContainerHeight + 'px',
        }"></div>

      </div>
      <div class="player-pokerhand-money-container bg-gray-500 dark:bg-gray-600" :style="{
        width: informationContainerWidth + 'px',
        height: pokerhandContainerHeight + 'px',
        fontSize: pokerhandContainerFontSize + 'px',
      }">
        <span :style="{
          marginLeft: profilePictureTextMargin + 'px',
        }">187 €</span>
        <div class="round-end bg-gray-500 dark:bg-gray-600" :style="{
          width: roundEdgeCircleDiameter + 'px',
          height: roundEdgeCircleDiameter + 'px',
          clipPath: 'inset(' + userNameContainerHeight + 'px 0 0 0)',
          transform: 'translateX(-50%)',
          marginBottom: (userNameContainerHeight + 1) + 'px', // Ensure at least 1px extra if needed
        }"></div>

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