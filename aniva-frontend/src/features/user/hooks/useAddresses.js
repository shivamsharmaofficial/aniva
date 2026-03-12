import {
  useQuery,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";

import {
  getAddresses,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress,
} from "../api/addressApi";

import { useToast } from "@/components/ui/useToast";

export const useAddresses = () =>
  useQuery({
    queryKey: ["addresses"],
    queryFn: getAddresses,
  });

export const useAddAddress = () => {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: addAddress,
    onSuccess: () => {
      queryClient.invalidateQueries(["addresses"]);
      showToast("Address added successfully");
    },
  });
};

export const useUpdateAddress = () => {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: ({ id, data }) =>
      updateAddress(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries(["addresses"]);
      showToast("Address updated successfully");
    },
  });
};

export const useDeleteAddress = () => {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: deleteAddress,
    onSuccess: () => {
      queryClient.invalidateQueries(["addresses"]);
      showToast("Address deleted");
    },
  });
};

export const useSetDefaultAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: setDefaultAddress,
    onSuccess: () => {
      queryClient.invalidateQueries(["addresses"]);
    },
  });
};