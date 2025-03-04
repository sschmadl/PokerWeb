<script setup lang="ts">
import {object, string, type InferType} from 'yup'
import type {FormSubmitEvent} from '#ui/types'
import { useAuth } from '~/composables/useAuth'

const schema = object({
  username: string().required('Required'),
  password: string()
      .required('Required')
})

type Schema = InferType<typeof schema>

const state = reactive({
  username: undefined,
  password: undefined
})

const loginError = ref('')

type LoginResponse = {
  token: string;
}

async function submit(event: FormSubmitEvent<Schema>) {
  try {
    const loginResponse = await $fetch<LoginResponse>('/auth/login', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: {
        username: event.data.username,
        password: event.data.password,
      }
    })

    console.log(loginResponse)

    useAuth().login(event.data.username, loginResponse.token);
  } catch (error: any) {
    if (error.data?.error) {
      loginError.value = error.data.error
    } else {
      loginError.value = 'An unexpected error occurred'
    }
  }
}


</script>

<template>
  <NavBar/>
  <div class="flex items-center justify-center pt-20">
    <UCard class="w-full max-w-md p-6">
      <h2 class="text-2xl font-semibold text-center">Login</h2>
      <UForm :schema="schema" :state="state" @submit="submit" class="space-y-4">
        <UFormGroup name="username" class="pt-4">
          <UInput v-model="state.username" placeholder="Username..." required/>
        </UFormGroup>

        <UFormGroup name="password" class="pt-4">
          <UInput v-model="state.password" type="password" placeholder="Password..." required/>
        </UFormGroup>


        <UFormGroup name="submit" class="pt-4">
          <UButton type="submit" class="my-4" block>Login</UButton>
        </UFormGroup>
        <p class="text-red-700">{{ loginError }}</p>
      </UForm>
    </UCard>
  </div>
</template>
