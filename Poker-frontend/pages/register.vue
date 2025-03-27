<script setup lang="ts">
import {object, string, type InferType} from 'yup'
import type {FormSubmitEvent} from '#ui/types'

const schema = object({
  username: string().required('Required'),
  password: string()
      .min(8, 'Must be at least 8 characters')
      .required('Required')
})

type Schema = InferType<typeof schema>

const state = reactive({
  username: undefined,
  password: undefined
})

const registerError = ref('');

type RegisterResponse = {
  status: string;
  error?: string;
};

async function submit(event: FormSubmitEvent<Schema>) {
  try {
    const registerResponse = await $fetch<RegisterResponse>('/auth/register', {
      method: 'POST',
      body: {
        username: event.data.username,
        password: event.data.password,
      }
    });

    navigateTo('/')
  } catch (error: any) {
    console.error("Fetch error:", error);

    if (error?.data?.error) {
      registerError.value = error.data.error;
    } else if (error?.message) {
      registerError.value = error.message;
    } else {
      registerError.value = 'An unexpected error occurred';
    }
  }
}

</script>

<template>
  <NavBar/>
  <div class="flex items-center justify-center pt-20">
    <UCard class="w-full max-w-md p-6">
      <h2 class="text-2xl font-semibold text-center">Register</h2>
      <UForm :schema="schema" :state="state" @submit="submit" class="space-y-4">
        <UFormGroup name="username" class="pt-4">
          <UInput v-model="state.username" placeholder="Username..." required/>
        </UFormGroup>

        <UFormGroup name="password" class="pt-4">
          <UInput v-model="state.password" type="password" placeholder="Password..." required/>
        </UFormGroup>


        <UFormGroup name="submit" class="pt-4">
          <UButton type="submit" class="my-4" block>Register</UButton>
        </UFormGroup>
        <div class="w-full text-center flex items-center justify-center">
          <p class="text-red-700 w-full">{{ registerError }}</p>
        </div>
      </UForm>
    </UCard>
  </div>
</template>
