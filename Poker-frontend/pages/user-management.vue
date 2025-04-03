<script setup>

import {useUsername} from "~/composables/states";
import {ref} from 'vue';
import {navigateTo} from "#app";


// Reference for the file input
const fileInput = ref(null);

// Function to trigger file input click
const triggerFileInput = () => {
  fileInput.value.click(); // This triggers the file input when the button is clicked
};

const registerError = ref('');

const handleFileUpload = async (event) => {
  const file = event.target.files[0]; // Get the selected file
  if (file) {
    // Check if the file is an image
    if (!(file.type === 'image/jpeg' || file.type === 'image/jpg')) {
      console.error('Selected file is not an acceptable file format.');
      alert('Please select a valid image file. (.jpeg)');
      return;
    }

    // Limit file size to 5MB
    const maxSize = 5 * 1024 * 1024; // 5MB in bytes
    if (file.size > maxSize) {
      console.error('File is too large. Maximum size is 5MB.');
      alert('File is too large. Maximum size is 5MB.');
      return;
    }

    console.log('Selected file:', file);

    // Display the image preview
    const reader = new FileReader();
    reader.onload = (e) => {
      const imgUrl = e.target.result;
      console.log('Image URL:', imgUrl);
      // You can use this URL to show a preview in the UI
    };
    reader.readAsDataURL(file);

    // Prepare the file for upload
    const formData = new FormData();
    formData.append('image', file);

    try {
      const token = useCookie('jwt', { default: () => '' }).value; // Get JWT token

      const response = await useFetch('/user-info/change-profile-picture', {
        method: 'POST',
        headers: {Authorization: token}, // Only add header if token exists
        body: formData,
      });

      const data = await response.json();
      console.log('Upload successful:', data);
      // Handle success (e.g., show uploaded image URL)
    } catch (error) {
      console.error('Error uploading image:', error);

      if (error?.data?.error) {
        registerError.value = error.data.error;
      } else if (error?.message) {
        registerError.value = error.message;
      } else {
        registerError.value = 'An unexpected error occurred';
      }
    }
  }
};



// Function to handle navigation to the change-password page
const goToChangePassword = () => {
  navigateTo('/change-password');
};

if (useUsername().value===""){
  useUsername().value="Poker Player"
}


</script>
<template>
  <NavBar/>
  <background-animation/>
  <div class="flex items-center justify-center pt-20">
    <UCard class="p-6 w-96">
      <!-- Profile Image & Name in a Row -->
      <div class="flex items-center space-x-4">
        <!-- Circular Profile Image -->
        <img
            :src="`/user-info/${useUsername().value}/profile-picture`"
            alt="Profile Picture"
            class="w-16 h-16 rounded-full object-cover"
        />
        <!-- Name & Role -->
        <div>
          <h3 class="text-lg font-semibold">{{useUsername().value}}</h3>
          <p class="text-gray-500 text-sm">Poker Enthusiast</p>
        </div>
      </div>

      <div class="mt-4">
        <!-- Change Profile Picture Button -->
        <UButton @click="triggerFileInput" block>
          Change Profile Picture
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

<style scoped>
/* You can add custom styles here */
</style>
