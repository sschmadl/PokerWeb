<script setup lang="ts">
import {object, string, type InferType} from 'yup'
import type {FormSubmitEvent} from '#ui/types'
import {useLoggedIn, useUsername} from "~/composables/states";

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

let loginError = ''

async function submit(event: FormSubmitEvent<Schema>) {
  const loginResponse = await useFetch('/auth/login', {
    method: 'POST',
    headers: {},
    body: {
      'username': event.data.username,
      'password': event.data.password,
    }
  })

  if (loginResponse.status.value === 'success') {
    console.log('success')
    useJWT().value = loginResponse.data.value.token
    useUsername().value = event.data.username
    useLoggedIn().value = true
    navigateTo('/')
  } else {
    console.log('Error')
    loginError = loginResponse.data.value.error
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
          <UInput v-model="state.username" placeholder="Username..." required />
        </UFormGroup>

        <UFormGroup name="password" class="pt-4">
          <UInput v-model="state.password" type="password" placeholder="Password..." required />
        </UFormGroup>


        <UFormGroup name="submit" class="pt-4">
          <UButton type="submit" class="my-4" block>Login</UButton>
        </UFormGroup>
        <p>{{ loginError }}</p>
      </UForm>
    </UCard>
  </div>
</template>
