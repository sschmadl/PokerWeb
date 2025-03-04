export const useAuth = () => {
    const username = useUsername();
    const jwt = useJWT();
    const loggedIn = useLoggedIn();

    const login = (newUsername: string, token: string) => {
        username.value = newUsername;
        jwt.value = token;
        loggedIn.value = true;
        navigateTo('/')
    };

    const logout = () => {
        username.value = '';
        jwt.value = '';
        loggedIn.value = false;
        navigateTo('/login');
    };

    return { login, logout };
};
