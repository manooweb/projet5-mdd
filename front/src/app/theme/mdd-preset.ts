import { definePreset } from '@primeuix/themes';
import Aura from '@primeuix/themes/aura';

export const MddPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '#f5f3ff',
      100: '#ebe8fe',
      200: '#d9d3fc',
      300: '#c0b7f7',
      400: '#a28fe9',
      500: '#7763c5',
      600: '#6753b1',
      700: '#554593',
      800: '#453876',
      900: '#392f60',
      950: '#221d3b',
    },
  },
  components: {
    progressspinner: {
      root: {
        colorOne: '{primary.500}',
        colorTwo: '{primary.600}',
        colorThree: '{primary.700}',
        colorFour: '{primary.800}',
      },
    },
  },
});
