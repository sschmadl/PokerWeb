export const useAuth = () => {
    const username = useUsername();
    const jwt = useJWT();

    const login = async (newUsername: string, token: string) => {
        username.value = newUsername;
        jwt.value = token;
        await navigateTo('/');
    };

    const logout = async () => {
        username.value = '';
        jwt.value = '';
        await navigateTo('/login');
    };

    return { login, logout };
};
