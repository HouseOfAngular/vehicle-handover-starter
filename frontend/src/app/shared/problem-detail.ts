import { HttpErrorResponse } from '@angular/common/http';
import { FieldTree, ValidationError } from '@angular/forms/signals';

export interface ProblemDetail {
  status: number;
  title: string;
  detail?: string;
  errors?: ProblemFieldError[];
}

export interface ProblemFieldError {
  field: string;
  message: string;
}

export function problemDetailMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const problem = error.error as ProblemDetail | null;
    return problem?.detail ?? problem?.title ?? error.message;
  }
  return 'Unexpected error';
}

export function problemDetailToFormErrors<T extends object>(
  error: unknown,
  form: FieldTree<T>,
): ValidationError.WithOptionalFieldTree[] {
  const fieldErrors =
    error instanceof HttpErrorResponse ? (error.error as ProblemDetail | null)?.errors : undefined;
  if (!fieldErrors?.length) {
    return [{ kind: 'server', message: problemDetailMessage(error), fieldTree: form }];
  }
  const fields = form as unknown as Record<string, FieldTree<unknown> | undefined>;
  return fieldErrors.map(({ field, message }) => ({
    kind: 'server',
    message,
    fieldTree: fields[field] ?? form,
  }));
}
