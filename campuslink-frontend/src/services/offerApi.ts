import api from "./api";

export interface Offer {
  id: string;
  posterId: string;
  type: "JOB" | "INTERNSHIP" | "PFE";
  title: string;
  company?: string;
  city?: string;
  locationType: "ON_SITE" | "REMOTE" | "HYBRID";
  experienceLevel?: string;
  duration?: string;
  description: string;
  domain?: string;
  deadline?: string;
  status: "OPEN" | "CLOSED";
  applicationCount?: number;
  createdAt: string;
  updatedAt?: string;
}

export interface OfferSummary {
  id: string;
  posterId: string;
  type: string;
  title: string;
  company?: string;
  city?: string;
  locationType: string;
  experienceLevel?: string;
  duration?: string;
  domain?: string;
  deadline?: string;
  status: string;
  createdAt: string;
}

export interface CreateOfferData {
  type: "JOB" | "INTERNSHIP" | "PFE";
  title: string;
  company?: string;
  city?: string;
  locationType: "ON_SITE" | "REMOTE" | "HYBRID";
  experienceLevel?: string;
  duration?: string;
  description: string;
  domain?: string;
  deadline?: string;
}

export interface ApplicationData {
  id: string;
  offerId: string;
  applicantId: string;
  cvUrlSnapshot: string;
  message?: string;
  status: string;
  appliedAt: string;
  updatedAt: string;
}

export const offerApi = {
  list: (params?: Record<string, string | number>) =>
    api.get<{ content: OfferSummary[]; totalPages: number; totalElements: number }>("/offers", { params }),

  getById: (id: string) => api.get<Offer>(`/offers/${id}`),

  create: (data: CreateOfferData) => api.post<Offer>("/offers", data),

  close: (id: string) => api.patch<Offer>(`/offers/${id}/close`),

  delete: (id: string) => api.delete(`/offers/${id}`),

  apply: (offerId: string, message?: string) =>
    api.post<ApplicationData>(`/offers/${offerId}/applications`, { message }),

  getApplications: (offerId: string) =>
    api.get<ApplicationData[]>(`/offers/${offerId}/applications`),

  getMyApplications: () => api.get<ApplicationData[]>("/me/applications"),

  updateApplicationStatus: (applicationId: string, status: "ACCEPTED" | "REJECTED") =>
    api.patch<ApplicationData>(`/applications/${applicationId}/status`, { status }),
};
