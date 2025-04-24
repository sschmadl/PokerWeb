<template>
  <NavBar/>
  <div class="flex items-center justify-center pt-20">
    <UCard class="p-6 w-96">
      <!-- Profile Image & Name in a Row -->
      <div class="flex items-center space-x-4">
        <!-- Circular Profile Image -->
        <img
            :src="`${profilePictureUrl}?t=${cacheBuster}`"
            alt="Profile Picture"
            class="w-16 h-16 rounded-full object-cover"
        />
        <!-- Name & Role -->
        <div>
          <h3 class="text-lg font-semibold">{{ useUsername().value }}</h3>
          <p class="text-gray-500 text-sm">Poker Enthusiast</p>
        </div>
      </div>

      <div class="mt-2">
        <!-- Change Profile Picture Button -->
        <UButton @click="triggerFileInput" block>
          Change Profile Picture
        </UButton>
      </div>
      <div class="mt-2">
        <!-- Delete Profile Picture Button -->
        <UButton @click="deletePfp" block>
          Delete Profile Picture
        </UButton>
      </div>

      <div class="mt-2">
        <!-- Change Password Button -->
        <UButton @click="goToChangePassword" block>
          Change Password
        </UButton>
      </div>

      <!-- Hidden file input for image upload -->
      <input ref="fileInput" type="file" accept="image/*" @change="handleFileUpload" class="hidden"/>
    </UCard>
  </div>
</template>

<script setup>
import { useUsername } from "~/composables/states";
import { ref } from 'vue';
import { navigateTo } from "#app";

const fileInput = ref(null);
const cacheBuster = ref(Date.now()); // Reactive timestamp to force image refresh
const profilePictureUrl = ref(`/user-info/${useUsername().value}/profile-picture`);

const triggerFileInput = () => {
  fileInput.value.click();
};

const deletePfp = async () => {
  const token = useCookie('jwt', {default: () => ''}).value;

  try {
    await $fetch('/user-info/delete-profile-picture', {
      method: 'POST',
      headers: { Authorization: token },
    });

    console.log('Profile picture deleted');
    cacheBuster.value = Date.now(); // Update cache-buster to force image reload
  } catch (error) {
    console.error('Error deleting profile picture:', error);
  }
};

const registerError = ref('');

const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (file) {
    if (!(file.type === 'image/jpeg' || file.type === 'image/jpg')) {
      alert('Please select a valid image file. (.jpeg)');
      return;
    }

    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
      alert('File is too large. Maximum size is 5MB.');
      return;
    }

    const formData = new FormData();
    formData.append('image', file);

    try {
      const token = useCookie('jwt', {default: () => ''}).value;

      await $fetch('/user-info/change-profile-picture', {
        method: 'POST',
        headers: { Authorization: token },
        body: formData,
      });

      console.log('Profile picture uploaded');
      cacheBuster.value = Date.now(); // Update cache-buster to force image reload
    } catch (error) {
      console.error('Error uploading image:', error);
      registerError.value = error?.message || 'An unexpected error occurred';
    }
  }
};

const goToChangePassword = () => {
  navigateTo('/change-password');
};

if (useUsername().value === "") {
  useUsername().value = "Poker Player";
}
</script>
