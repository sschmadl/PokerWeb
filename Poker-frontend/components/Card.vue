<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  faceDown: {
    type: Boolean,
    required: true,
  },
  frontImage: String,
  height: {
    type: Number,
    required: true,
  },
  highlighted: Boolean,
});

const card_back = '/cards_default/2B.svg';
const card_front = computed(() => props.frontImage ?? '/cards_default/1J.svg');
const highlighted = computed(() => props.highlighted ?? false);

const isFlipped = ref(props.faceDown);
watch(() => props.faceDown, (newVal) => {
  isFlipped.value = newVal;
});
</script>


<template>
  <div
      class="card"
      :style="{
      height: props.height + 'px',
      width: props.height * 0.7 + 'px', // card aspect ratio 0.7
      boxShadow: highlighted ? '0 0 40px rgba(255, 255, 255, 0.5)' : '',
    }"
  >
    <div class="card-inner" :class="{ flipped: isFlipped }">
      <div class="card-face card-front">
        <img :src="card_front" alt="Front of card" />
      </div>
      <div class="card-face card-back">
        <img :src="card_back" alt="Back of card" />
      </div>
    </div>
  </div>
</template>


<style scoped>
.card {
  perspective: 1000px;
  display: inline-block;
  position: relative;
}

.card-inner {
  width: 100%;
  height: 100%;
  position: relative;
  transition: transform 0.4s;
  transform-style: preserve-3d;
}

.card-inner.flipped {
  transform: rotateY(180deg);
}

.card-face {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
}

.card-face img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.card-back {
  transform: rotateY(180deg);
}

</style>
