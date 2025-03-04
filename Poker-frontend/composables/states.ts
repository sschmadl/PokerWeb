export const useUsername = () => useCookie<string>('username', { default: () => ''})
export const useJWT = () => useCookie<string>('jwt', { default: () => '' })
export const useLoggedIn = () => useCookie<boolean>('loggedIn', { default: () => false })
