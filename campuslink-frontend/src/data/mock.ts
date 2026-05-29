export const mockUser = {
  id: "1",
  fullName: "Ahmed Mansouri",
  email: "ahmed.mansouri@university.ma",
  university: "ENSIAS",
  city: "Rabat",
  bio: "Passionate final-year student bridging the gap between high-performance engineering and human-centered design.",
  profilePicUrl: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
  role: "STUDENT" as const,
  expertise: ["Fullstack Dev", "UI Design", "Product Strategy"],
  stats: { listings: 12, applications: 5, rating: 4.9 },
};

export const mockColocations = [
  { id: "1", title: "Luxury Flat Marina", city: "Casablanca", address: "Near Twin Center", rentPerPerson: 3500, rating: 4.9, housingType: "APARTMENT", amenities: ["Wi-Fi", "AC"], image: "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=400&h=250&fit=crop", badge: "PREMIUM", nearTo: "10 min to ENSEM" },
  { id: "2", title: "Agdal Creative Hub", city: "Rabat", address: "Avenue de France", rentPerPerson: 2800, rating: 4.7, housingType: "APARTMENT", amenities: ["Washer", "Shared Kitchen"], image: "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=400&h=250&fit=crop", nearTo: "5 min to UM5" },
  { id: "3", title: "Gueliz Modern Suites", city: "Marrakech", address: "Near Plaza", rentPerPerson: 4200, rating: 5.0, housingType: "STUDIO", amenities: ["Pool", "24/7 Security"], image: "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=400&h=250&fit=crop", badge: "TOP RATED", nearTo: "15 min to UCAM" },
  { id: "4", title: "Oulfa Student Village", city: "Casablanca", address: "Near Casa Finance City", rentPerPerson: 2200, rating: 4.5, housingType: "HOUSE", amenities: ["Fast Fiber"], image: "https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=400&h=250&fit=crop", nearTo: "8 min to ESITH" },
  { id: "5", title: "The Scholars Loft", city: "Rabat", address: "Hay Riad", rentPerPerson: 3900, rating: 4.8, housingType: "APARTMENT", amenities: ["Gym"], image: "https://images.unsplash.com/photo-1536376072261-38c75010e6c9?w=400&h=250&fit=crop", nearTo: "12 min to UIR" },
  { id: "6", title: "Skyline Residences", city: "Casablanca", address: "Sidi Maarouf", rentPerPerson: 3100, rating: 4.6, housingType: "APARTMENT", amenities: ["Parking"], image: "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=400&h=250&fit=crop", nearTo: "5 min to ENSAM" },
];

export const mockOffers = [
  { id: "1", title: "Backend Engineering Intern", company: "Stripe", domain: "Payments Infrastructure", city: "Casablanca", locationType: "REMOTE", type: "INTERNSHIP", duration: "6 Months", experienceLevel: "PFE Qualified", salary: "4,500 DH / mo", logo: "https://logo.clearbit.com/stripe.com" },
  { id: "2", title: "Cloud Solution Architect (Graduate)", company: "Microsoft", domain: "Azure Global", city: "Rabat Tech Park", locationType: "ON_SITE", type: "JOB", duration: "Full-time", experienceLevel: "Graduate", salary: "Competitive Pay", postedAgo: "2h ago", logo: "https://logo.clearbit.com/microsoft.com" },
  { id: "3", title: "Sustainable Energy Systems PFE", company: "OCP Group", domain: "Innovation Lab", city: "Benguerir", locationType: "ON_SITE", type: "PFE", salary: "Stipend + Housing", logo: "" },
];

export const mockItems = [
  { id: "1", title: "MacBook Pro M3", price: 12500, category: "Electronics", condition: "LIKE_NEW", seller: "Anas B.", university: "UM6P Campus", image: "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=250&fit=crop" },
  { id: "2", title: "Advanced Algorithms Textbook", price: 450, category: "Books", condition: "GOOD", seller: "Sara K.", university: "Al Akhawayn", image: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=250&fit=crop" },
  { id: "3", title: "Ergonomic Study Chair", price: 890, category: "Furniture", condition: "GOOD", seller: "Yassine M.", university: "Casa Campus", image: "https://images.unsplash.com/photo-1580480055497-2023f6f0e4de?w=400&h=250&fit=crop" },
  { id: "4", title: "UI/UX Design Services", price: 150, category: "Services", condition: "NEW", seller: "Lina R.", university: "Rabat Design", image: "https://images.unsplash.com/photo-1552664730-d307ca884978?w=400&h=250&fit=crop", priceLabel: "DH/hr" },
  { id: "5", title: "Sony WH-1000XM5", price: 2100, category: "Electronics", condition: "LIKE_NEW", seller: "Mehdi A.", university: "UM6P Campus", image: "https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=400&h=250&fit=crop" },
];

export const mockDashboard = {
  pfeProgress: 75,
  profileScore: "Excellent",
  activeApplications: 12,
  missingDocs: 0,
  workshops: [
    { date: "OCT 24", title: "UI/UX Design Masterclass", location: "Amphi A, Science Faculty", time: "14:00" },
    { date: "OCT 27", title: "Entrepreneurship Forum", location: "Startup Hall", time: "09:00" },
  ],
  marketplaceActivity: [
    { title: "MacBook Air M2 (8GB/256GB)", subtitle: "New listing in Electronics", price: "9,500 DH", time: "2 mins ago" },
    { title: "Introduction to Finance", subtitle: "Ahmed B. commented", status: "Sold", time: "1 hour ago" },
    { title: "Texas Instruments TI-84", subtitle: "Price drop alert", price: "450 DH", time: "3 hours ago" },
  ],
};
