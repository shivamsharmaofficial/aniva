import { useQuery } from "@tanstack/react-query";
import { fetchAddresses } from "../api/addressApi";

export const useAddresses = () => {

  return useQuery({
    queryKey: ["addresses"],
    queryFn: fetchAddresses
  });

};