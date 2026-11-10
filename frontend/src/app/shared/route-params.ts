import { numberAttribute } from '@angular/core';

export function optionalNumberAttribute(value: string | undefined): number | undefined {
  return value === undefined ? undefined : numberAttribute(value);
}
