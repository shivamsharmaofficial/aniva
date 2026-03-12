import { useMemo } from "react";

export function useFormValidation(values, validators) {
  return useMemo(() => {
    const errors = {};

    Object.keys(validators).forEach((key) => {
      const error = validators[key](values[key], values);
      if (error) errors[key] = error;
    });

    return errors;
  }, [values, validators]);
}