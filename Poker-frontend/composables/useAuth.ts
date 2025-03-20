export const useAuth = () => {
    const username = useUsername();
    const jwt = useJWT();

    const login = (newUsername: string, token: string) => {
        username.value = newUsername;
        jwt.value = token;
        navigateTo('/')
    };

    const logout = () => {
        username.value = '';
        jwt.value = '';
        navigateTo('/login');
    };

    return { login, logout };
};
