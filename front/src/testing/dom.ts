export const getInput = (hostElement: HTMLElement, id: string): HTMLInputElement => {
  const input = hostElement.querySelector<HTMLInputElement>(`#${id}`);

  if (input === null) {
    throw new Error(`Expected an input with id "${id}".`);
  }

  return input;
};
