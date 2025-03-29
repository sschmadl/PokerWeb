<template>
  <NavBar/>
  <div class="flex items-center justify-center pt-20">
    <UCard class="p-6 w-96">
      <!-- Profile Image & Name in a Row -->
      <div class="flex items-center space-x-4">
        <!-- Circular Profile Image -->
        <NuxtImg
            src="https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
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

<script setup>
import {useUsername} from "~/composables/states";
import {useRouter} from 'vue-router';
import {ref} from 'vue';

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

if (useUsername().value===""){
  useUsername().value="Poker Player"
}
</script>

<style scoped>
/* You can add custom styles here */
</style>
