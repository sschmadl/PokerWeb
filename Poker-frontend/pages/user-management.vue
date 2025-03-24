<template>
  <PokerSuitBackground />
  <NavBar />
  <div class="flex items-center justify-center pt-20">
    <UCard class="w-full max-w-md p-6">
      <NuxtImg src="/ExamLog.png" alt="Alternative Text" />

      <!-- Change Profile Picture Button -->
      <UButton @click="triggerFileInput">
        Change Profile Picture
      </UButton>

      <br>

      <!-- Change Password Button with Click Handler -->
      <UButton @click="goToChangePassword">
        Change Password
      </UButton>

      <!-- Hidden file input for image upload -->
      <input ref="fileInput" type="file" accept="image/*" @change="handleFileUpload" class="hidden" />
    </UCard>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ref } from 'vue';

// Set up the router
const router = useRouter();

// Reference for the file input
const fileInput = ref(null);

// Function to trigger file input click
const triggerFileInput = () => {
  fileInput.value.click(); // This triggers the file input when the button is clicked
};

// Function to handle file upload
const handleFileUpload = (event) => {
  const file = event.target.files[0]; // Get the selected file
  if (file) {
    // Handle the file (e.g., preview the image, upload it, etc.)
    console.log('Selected file:', file);

    // Example: Display the image preview
    const reader = new FileReader();
    reader.onload = (e) => {
      const imgUrl = e.target.result;
      console.log('Image URL:', imgUrl);
      // You can display the image preview or handle the upload logic here
    };
    reader.readAsDataURL(file);
  }
};

// Function to handle navigation to the change-password page
const goToChangePassword = () => {
  router.push('/change-password');
};
</script>

<style scoped>
/* You can add custom styles here */
</style>
