export const useUsername = () => useCookie<string>('username', { default: () => ''})
export const useJWT = () => useCookie<string>('jwt', { default: () => '' })

export async function isLoggedIn() {
    try {
        await $fetch<string>('/auth/is-valid-token', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: {
                token: useJWT().value
            }
        })

        return true;
    } catch (error: any) {
        if (error.data?.error) {
            console.log(error.data.error);
        } else {
            console.log('Error ocurred while trying to check login-status.');
        }

        return false;
    }
}
