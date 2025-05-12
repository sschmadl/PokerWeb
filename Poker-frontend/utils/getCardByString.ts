export const getCardByString = (name: string): string => {
    const card_url = '/cards_default/' + name + '.svg'
    console.log('Card: ', card_url);
    return card_url;
};
