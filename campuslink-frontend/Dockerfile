# --- Build stage ---
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# In the cluster the frontend and API share one Ingress host, so the app calls
# same-origin relative paths. These are baked into the static bundle at build time.
ENV VITE_API_URL=/api
ENV VITE_API_ORIGIN=""
RUN npm run build

# --- Serve stage ---
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
