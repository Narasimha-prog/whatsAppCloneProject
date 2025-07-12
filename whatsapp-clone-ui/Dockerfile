# Stage 1: Build the Angular app
FROM node:20.19.0-alpine AS builder

WORKDIR /app


# Copy and install dependencies
COPY package*.json ./
RUN npm install

# Copy app source and build
COPY . .
RUN npm run build -- --configuration production

# Stage 2: Serve the app with Nginx
FROM nginx:alpine

# Clean default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy built Angular app from builder stage
COPY --from=builder /app/dist/whatsapp-clone-ui/browser /usr/share/nginx/html

# Optional: Use a custom nginx.conf if needed
# COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
